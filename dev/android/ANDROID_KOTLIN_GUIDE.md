# Android Kotlin Development Guide

Complete reference for Android development with Kotlin, Jetpack Compose, MVVM, Room, and Retrofit.

---

## 📋 Table of Contents

1. [Quick Start](#quick-start)
2. [Architecture Overview](#architecture-overview)
3. [Data Layer](#data-layer)
4. [Domain Layer](#domain-layer)
5. [Presentation Layer](#presentation-layer)
6. [UI Layer](#ui-layer)
7. [Dependency Injection](#dependency-injection)
8. [Common Patterns](#common-patterns)
9. [Testing](#testing)
10. [Project Structure](#project-structure)

---

## 🚀 Quick Start

### Prerequisites

- Android Studio Hedgehog or newer
- Kotlin 1.9+
- Min SDK 30 (Android 11)
- Target SDK 34 (Android 14)
- JDK 17

### Setup Steps

1. **Create new Android Studio project**
   - Choose "Empty Compose Activity"
   - Language: Kotlin
   - Minimum SDK: API 30

2. **Add dependencies** to `app/build.gradle.kts`:

```kotlin
dependencies {
    // Core
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")

    // Compose
    implementation(platform("androidx.compose:compose-bom:2024.01.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")

    // Lifecycle
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.7.0")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")

    // Room
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    ksp("androidx.room:room-compiler:2.6.1")

    // Retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    // Koin (DI)
    implementation("io.insert-koin:koin-android:3.5.3")
    implementation("io.insert-koin:koin-androidx-compose:3.5.3")
    implementation("io.insert-koin:koin-core:3.5.3")

    // Testing
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}
```

3. **Sync project** and you're ready!

---

## 🏗️ Architecture Overview

### MVVM + Clean Architecture

```
Presentation (UI)
    ↕️
ViewModel (State holders)
    ↕️
Domain (Use cases/Business logic)
    ↕️
Data (Repositories)
    ↕️
Sources (API, Database)
```

### Key Principles

1. **Separation of Concerns**: Each layer has distinct responsibilities
2. **Unidirectional Data Flow**: State flows down, events flow up
3. **Reactive**: Use StateFlow and SharedFlow for state management
4. **Offline-First**: Cache network data locally with Room
5. **Testable**: Each layer is independently testable

---

## 🗄️ Data Layer

### Room Database

#### Entity

```kotlin
@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val email: String,
    val avatarUrl: String?,
    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis()
)
```

#### DAO

```kotlin
@Dao
interface UserDao {
    @Query("SELECT * FROM users")
    fun getAllUsers(): Flow<List<UserEntity>>

    @Query("SELECT * FROM users WHERE id = :userId")
    fun getUserById(userId: String): Flow<UserEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsers(users: List<UserEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Update
    suspend fun updateUser(user: UserEntity)

    @Delete
    suspend fun deleteUser(user: UserEntity)

    @Query("DELETE FROM users")
    suspend fun deleteAllUsers()
}
```

#### Database

```kotlin
@Database(
    entities = [UserEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao

    companion object {
        const val DATABASE_NAME = "app_database"
    }
}
```

### Retrofit API Service

#### API Model

```kotlin
data class UserApiModel(
    val id: String,
    val name: String,
    val email: String,
    @SerializedName("avatar_url")
    val avatarUrl: String?
)
```

#### API Service

```kotlin
interface UserApiService {
    @GET("users")
    suspend fun getUsers(): List<UserApiModel>

    @GET("users/{id}")
    suspend fun getUser(@Path("id") userId: String): UserApiModel

    @POST("users")
    suspend fun createUser(@Body user: UserApiModel): UserApiModel

    @PUT("users/{id}")
    suspend fun updateUser(@Path("id") userId: String, @Body user: UserApiModel): UserApiModel

    @DELETE("users/{id}")
    suspend fun deleteUser(@Path("id") userId: String)
}
```

#### Network Module

```kotlin
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://api.example.com/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideUserApiService(retrofit: Retrofit): UserApiService {
        return retrofit.create(UserApiService::class.java)
    }
}
```

---

## 🏭 Domain Layer

### Repository Interface

```kotlin
interface UserRepository {
    fun getUsers(): Flow<Result<List<User>>>
    fun getUser(userId: String): Flow<Result<User?>>
    suspend fun refreshUsers()
    suspend fun updateUser(user: User): Result<Unit>
}
```

### Repository Implementation

```kotlin
class UserRepositoryImpl(
    private val apiService: UserApiService,
    private val userDao: UserDao
) : UserRepository {

    override fun getUsers(): Flow<Result<List<User>>> = flow {
        // First emit cached data
        userDao.getAllUsers().collect { entities ->
            if (entities.isNotEmpty()) {
                emit(Result.Success(entities.map { it.toDomainModel() }))
            }
        }

        // Then refresh from network
        try {
            val apiUsers = apiService.getUsers()
            val entities = apiUsers.map { it.toEntity() }
            userDao.insertUsers(entities)
            emit(Result.Success(entities.map { it.toDomainModel() }))
        } catch (e: Exception) {
            emit(Result.Error(e))
        }
    }.flowOn(Dispatchers.IO)

    override fun getUser(userId: String): Flow<Result<User?>> = flow {
        userDao.getUserById(userId).collect { entity ->
            emit(Result.Success(entity?.toDomainModel()))
        }

        try {
            val apiUser = apiService.getUser(userId)
            val entity = apiUser.toEntity()
            userDao.insertUser(entity)
            emit(Result.Success(entity.toDomainModel()))
        } catch (e: Exception) {
            emit(Result.Error(e))
        }
    }.flowOn(Dispatchers.IO)

    override suspend fun refreshUsers() {
        try {
            val apiUsers = apiService.getUsers()
            val entities = apiUsers.map { it.toEntity() }
            userDao.insertUsers(entities)
        } catch (e: Exception) {
            // Log error but don't crash
            e.printStackTrace()
        }
    }

    override suspend fun updateUser(user: User): Result<Unit> {
        return try {
            val apiModel = user.toApiModel()
            apiService.updateUser(user.id, apiModel)
            userDao.updateUser(user.toEntity())
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}
```

### Mapping Extensions

```kotlin
// Entity to Domain
fun UserEntity.toDomainModel(): User = User(
    id = id,
    name = name,
    email = email,
    avatarUrl = avatarUrl
)

// API Model to Entity
fun UserApiModel.toEntity(): UserEntity = UserEntity(
    id = id,
    name = name,
    email = email,
    avatarUrl = avatarUrl
)

// Domain to API Model
fun User.toApiModel(): UserApiModel = UserApiModel(
    id = id,
    name = name,
    email = email,
    avatarUrl = avatarUrl
)
```

---

## 🎨 Presentation Layer

### State Management with StateFlow

#### UI State

```kotlin
sealed interface UsersUiState {
    data object Loading : UsersUiState
    data class Success(val users: List<User>) : UsersUiState
    data class Error(val message: String) : UsersUiState

    val isLoading: Boolean
        get() = this is Loading

    val isSuccess: Boolean
        get() = this is Success

    val isError: Boolean
        get() = this is Error
}
```

#### ViewModel

```kotlin
class UsersViewModel(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<UsersUiState>(UsersUiState.Loading)
    val uiState: StateFlow<UsersUiState> = _uiState.asStateFlow()

    private val _uiEffect = MutableSharedFlow<UsersUiEffect>()
    val uiEffect: SharedFlow<UsersUiEffect> = _uiEffect.asSharedFlow()

    init {
        loadUsers()
    }

    fun loadUsers() {
        viewModelScope.launch {
            _uiState.value = UsersUiState.Loading

            userRepository.getUsers()
                .onEach { result ->
                    when (result) {
                        is Result.Success -> {
                            if (result.data.isEmpty()) {
                                _uiState.value = UsersUiState.Error("No users found")
                            } else {
                                _uiState.value = UsersUiState.Success(result.data)
                            }
                        }
                        is Result.Error -> {
                            _uiState.value = UsersUiState.Error(
                                result.exception.message ?: "Unknown error"
                            )
                        }
                    }
                }
                .launchIn(viewModelScope)
        }
    }

    fun onUserClick(user: User) {
        viewModelScope.launch {
            _uiEffect.emit(UsersUiEffect.NavigateToUserDetail(user.id))
        }
    }

    fun refreshUsers() {
        viewModelScope.launch {
            userRepository.refreshUsers()
        }
    }
}
```

#### UI Effect

```kotlin
sealed interface UsersUiEffect {
    data class NavigateToUserDetail(val userId: String) : UsersUiEffect
    data class ShowToast(val message: String) : UsersUiEffect
}
```

### Result Wrapper

```kotlin
sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val exception: Exception) : Result<Nothing>()

    val isSuccess: Boolean
        get() = this is Success

    val isError: Boolean
        get() = this is Error

    fun getOrNull(): T? = if (this is Success) data else null

    fun exceptionOrNull(): Exception? = if (this is Error) exception else null
}
```

---

## 🎯 UI Layer

### Composable Screen

```kotlin
@Composable
fun UsersScreen(
    viewModel: UsersViewModel = koinViewModel(),
    onUserClick: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    // Handle side effects
    LaunchedEffect(Unit) {
        viewModel.uiEffect.collect { effect ->
            when (effect) {
                is UsersUiEffect.NavigateToUserDetail -> {
                    onUserClick(effect.userId)
                }
                is UsersUiEffect.ShowToast -> {
                    Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    UsersContent(
        uiState = uiState,
        onRefresh = { viewModel.refreshUsers() },
        onUserClick = { user -> viewModel.onUserClick(user) }
    )
}

```

### UI Content

```kotlin
@Composable
fun UsersContent(
    uiState: UsersUiState,
    onRefresh: () -> Unit,
    onUserClick: (User) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Users") },
                actions = {
                    IconButton(onClick = onRefresh) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (uiState) {
                is UsersUiState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                is UsersUiState.Success -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(uiState.users) { user ->
                            UserItem(
                                user = user,
                                onClick = { onUserClick(user) }
                            )
                        }
                    }
                }
                is UsersUiState.Error -> {
                    ErrorState(
                        message = uiState.message,
                        onRetry = onRefresh
                    )
                }
            }
        }
    }
}
```

### User Item

```kotlin
@Composable
fun UserItem(
    user: User,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // User avatar
            AsyncImage(
                model = user.avatarUrl,
                contentDescription = "${user.name} avatar",
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape),
                placeholder = painterResource(R.drawable.ic_user_placeholder)
            )

            Spacer(modifier = Modifier.width(16.dp))

            // User info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = user.name,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = user.email,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
```

### Error State

```kotlin
@Composable
fun ErrorState(
    message: String,
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Error,
            contentDescription = "Error",
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.error
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(onClick = onRetry) {
            Text("Retry")
        }
    }
}
```

---

## 💉 Dependency Injection with Koin

### Application Class

```kotlin
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@MyApplication)
            androidLogger()

            modules(
                databaseModule,
                networkModule,
                repositoryModule,
                viewModelModule
            )
        }
    }
}
```

### Database Module

```kotlin
val databaseModule = module {
    single {
        Room.databaseBuilder(
            androidContext(),
            AppDatabase::class.java,
            AppDatabase.DATABASE_NAME
        ).build()
    }

    single { get<AppDatabase>().userDao() }
}
```

### Network Module

```kotlin
val networkModule = module {
    single {
        OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    single {
        Retrofit.Builder()
            .baseUrl("https://api.example.com/")
            .client(get())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    single { get<Retrofit>().create(UserApiService::class.java) }
}
```

### Repository Module

```kotlin
val repositoryModule = module {
    single<UserRepository> { UserRepositoryImpl(get(), get()) }
}
```

### ViewModel Module

```kotlin
val viewModelModule = module {
    viewModel { UsersViewModel(get()) }
    viewModel { (userId: String) -> UserDetailViewModel(userId, get()) }
}
```

---

## 🔄 Common Patterns

### Pagination

```kotlin
@Dao
interface ItemDao {
    @Query("SELECT * FROM items ORDER BY id LIMIT :limit OFFSET :offset")
    fun getItems(limit: Int, offset: Int): Flow<List<ItemEntity>>
}

class ItemRepositoryImpl(
    private val dao: ItemDao,
    private val api: ItemApiService
) : ItemRepository {

    fun getItemsPaged(page: Int, pageSize: Int = 20): Flow<Result<List<Item>>> {
        return flow {
            val offset = (page - 1) * pageSize
            dao.getItems(pageSize, offset).collect { entities ->
                emit(Result.Success(entities.map { it.toDomain() }))
            }

            try {
                val apiItems = api.getItems(page, pageSize)
                val entities = apiItems.map { it.toEntity() }
                dao.insertItems(entities)
            } catch (e: Exception) {
                emit(Result.Error(e))
            }
        }.flowOn(Dispatchers.IO)
    }
}
```

### Search

```kotlin
@Dao
interface SearchDao {
    @Query("SELECT * FROM items WHERE name LIKE '%' || :query || '%'")
    fun searchItems(query: String): Flow<List<ItemEntity>>
}

class SearchRepository(
    private val dao: SearchDao
) {
    fun search(query: String): Flow<List<Item>> =
        dao.searchItems(query).map { entities ->
            entities.map { it.toDomain() }
        }
}
```

### Offline-First Strategy

```kotlin
class OfflineFirstRepository(
    private val api: ApiService,
    private val dao: ItemDao
) {

    fun getData(): Flow<Result<List<Item>>> = flow {
        // Emit cached data immediately
        dao.getAll().collect { cached ->
            emit(Result.Success(cached.map { it.toDomain() }))
        }

        // Try to refresh from network
        try {
            val networkData = api.getData()
            val entities = networkData.map { it.toEntity() }
            dao.insertAll(entities)
        } catch (e: Exception) {
            // Log but don't crash - cache is still available
            e.printStackTrace()
        }
    }.flowOn(Dispatchers.IO)
}
```

### Error Handling

```kotlin
sealed class AppError : Exception() {
    data class NetworkError(override val message: String) : AppError()
    data class DatabaseError(override val message: String) : AppError()
    data class UnknownError(override val message: String) : AppError()
}

suspend fun <T> safeApiCall(apiCall: suspend () -> T): Result<T> {
    return try {
        Result.Success(apiCall())
    } catch (e: IOException) {
        Result.Error(AppError.NetworkError("Network error occurred"))
    } catch (e: Exception) {
        Result.Error(AppError.UnknownError(e.message ?: "Unknown error"))
    }
}
```

---

## 🧪 Testing

### Unit Testing ViewModel

```kotlin
class UsersViewModelTest {

    @Test
    fun `loadUsers emits loading then success`() = runTest {
        // Given
        val fakeUsers = listOf(User("1", "John", "john@example.com"))
        val repository = FakeUserRepository(fakeUsers)
        val viewModel = UsersViewModel(repository)

        // When
        viewModel.loadUsers()

        // Then
        assertEquals(UsersUiState.Loading, viewModel.uiState.value)

        advanceUntilIdle()

        assertEquals(
            UsersUiState.Success(fakeUsers),
            viewModel.uiState.value
        )
    }
}
```

### UI Testing

```kotlin
class UsersScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun loading_showsProgressIndicator() {
        composeTestRule.setContent {
            UsersContent(
                uiState = UsersUiState.Loading,
                onRefresh = {},
                onUserClick = {}
            )
        }

        composeTestRule.onNodeWithTag("loading_indicator")
            .assertIsDisplayed()
    }
}
```

---

## 📁 Project Structure

```
app/src/main/
├── java/com/example/app/
│   ├── App.kt
│   ├── MainActivity.kt
│   ├── data/
│   │   ├── api/
│   │   │   ├── ApiService.kt
│   │   │   └── model/
│   │   │       ├── UserApiModel.kt
│   │   │       └── mapping/
│   │   │           └── UserMapping.kt
│   │   ├── local/
│   │   │   ├── database/
│   │   │   │   └── AppDatabase.kt
│   │   │   ├── dao/
│   │   │   │   └── UserDao.kt
│   │   │   └── entity/
│   │   │       └── UserEntity.kt
│   │   └── repository/
│   │       ├── UserRepository.kt
│   │       └── UserRepositoryImpl.kt
│   ├── di/
│   │   ├── DatabaseModule.kt
│   │   ├── NetworkModule.kt
│   │   └── RepositoryModule.kt
│   ├── domain/
│   │   └── model/
│   │       └── User.kt
│   ├── ui/
│   │   ├── screens/
│   │   │   └── users/
│   │   │       ├── UsersScreen.kt
│   │   │       ├── UsersViewModel.kt
│   │   │       └── components/
│   │   │           ├── UserItem.kt
│   │   │           └── ErrorState.kt
│   │   ├── theme/
│   │   │   ├── Color.kt
│   │   │   ├── Theme.kt
│   │   │   └── Type.kt
│   │   └── components/
│   │       └── common/
│   └── util/
│       └── Result.kt
└── res/
    ├── drawable/
    ├── values/
    │   ├── strings.xml
    │   ├── colors.xml
    │   └── themes.xml
    └── mipmap/
```

---

## 📚 Resources

### Official Documentation

- [Android Developer Guides](https://developer.android.com/guide)
- [Jetpack Compose](https://developer.android.com/jetpack/compose/documentation)
- [Kotlin Coroutines](https://kotlinlang.org/docs/coroutines-guide.html)
- [Room Database](https://developer.android.com/training/data-storage/room)
- [Retrofit](https://square.github.io/retrofit/)

### Best Practices

- Use `collectAsStateWithLifecycle()` in Composables
- Handle errors gracefully - don't crash on network failures
- Use sealed interfaces for UI state
- Implement offline-first caching
- Write unit tests for ViewModels and Repositories
- Use Koin for dependency injection
- Follow Material 3 design guidelines

---

## ✅ Checklist

When implementing a new feature:

- [ ] Create API models for network requests
- [ ] Create Room entities for local storage
- [ ] Write mapping functions between layers
- [ ] Implement Repository interface
- [ ] Create UI state sealed interface/class
- [ ] Implement ViewModel with StateFlow
- [ ] Set up Koin DI modules
- [ ] Create Composable UI screens
- [ ] Add error handling
- [ ] Write unit tests
- [ ] Test offline functionality

---

**Guide Version:** 1.0
**Last Updated:** 2025-11-25
**Tech Stack:** Kotlin + Jetpack Compose + MVVM + Room + Retrofit + Coroutines
