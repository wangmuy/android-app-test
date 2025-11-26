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
@Entity(tableName = "items")
data class ItemEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val description: String,
    val imageUrl: String?,
    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis()
)
```

#### DAO

```kotlin
@Dao
interface ItemDao {
    @Query("SELECT * FROM items")
    fun getAllItems(): Flow<List<ItemEntity>>

    @Query("SELECT * FROM items WHERE id = :itemId")
    fun getItemById(itemId: String): Flow<ItemEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItems(items: List<ItemEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: ItemEntity)

    @Update
    suspend fun updateItem(item: ItemEntity)

    @Delete
    suspend fun deleteItem(item: ItemEntity)

    @Query("DELETE FROM items")
    suspend fun deleteAllItems()
}
```

#### Database

```kotlin
@Database(
    entities = [ItemEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun itemDao(): ItemDao

    companion object {
        const val DATABASE_NAME = "app_database"
    }
}
```

### Retrofit API Service

#### API Model

```kotlin
data class ItemApiModel(
    val id: String,
    val name: String,
    val description: String,
    @SerializedName("image_url")
    val imageUrl: String?
)
```

#### API Service

```kotlin
interface ItemApiService {
    @GET("items")
    suspend fun getItems(): List<ItemApiModel>

    @GET("items/{id}")
    suspend fun getItem(@Path("id") itemId: String): ItemApiModel

    @POST("items")
    suspend fun createItem(@Body item: ItemApiModel): ItemApiModel

    @PUT("items/{id}")
    suspend fun updateItem(@Path("id") itemId: String, @Body item: ItemApiModel): ItemApiModel

    @DELETE("items/{id}")
    suspend fun deleteItem(@Path("id") itemId: String)
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
    fun provideItemApiService(retrofit: Retrofit): ItemApiService {
        return retrofit.create(ItemApiService::class.java)
    }
}
```

---

## 🏭 Domain Layer

### Repository Interface

```kotlin
interface ItemRepository {
    fun getItems(): Flow<Result<List<Item>>>
    fun getItem(itemId: String): Flow<Result<Item?>>
    suspend fun refreshItems()
    suspend fun updateItem(item: Item): Result<Unit>
}
```

### Repository Implementation

```kotlin
class ItemRepositoryImpl(
    private val apiService: ItemApiService,
    private val itemDao: ItemDao
) : ItemRepository {

    override fun getItems(): Flow<Result<List<Item>>> = flow {
        // First emit cached data
        itemDao.getAllItems().collect { entities ->
            if (entities.isNotEmpty()) {
                emit(Result.Success(entities.map { it.toDomainModel() }))
            }
        }

        // Then refresh from network
        try {
            val apiItems = apiService.getItems()
            val entities = apiItems.map { it.toEntity() }
            itemDao.insertItems(entities)
            emit(Result.Success(entities.map { it.toDomainModel() }))
        } catch (e: Exception) {
            emit(Result.Error(e))
        }
    }.flowOn(Dispatchers.IO)

    override fun getItem(itemId: String): Flow<Result<Item?>> = flow {
        itemDao.getItemById(itemId).collect { entity ->
            emit(Result.Success(entity?.toDomainModel()))
        }

        try {
            val apiItem = apiService.getItem(itemId)
            val entity = apiItem.toEntity()
            itemDao.insertItem(entity)
            emit(Result.Success(entity.toDomainModel()))
        } catch (e: Exception) {
            emit(Result.Error(e))
        }
    }.flowOn(Dispatchers.IO)

    override suspend fun refreshItems() {
        try {
            val apiItems = apiService.getItems()
            val entities = apiItems.map { it.toEntity() }
            itemDao.insertItems(entities)
        } catch (e: Exception) {
            // Log error but don't crash
            e.printStackTrace()
        }
    }

    override suspend fun updateItem(item: Item): Result<Unit> {
        return try {
            val apiModel = item.toApiModel()
            apiService.updateItem(item.id, apiModel)
            itemDao.updateItem(item.toEntity())
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
fun ItemEntity.toDomainModel(): Item = Item(
    id = id,
    name = name,
    description = description,
    imageUrl = imageUrl
)

// API Model to Entity
fun ItemApiModel.toEntity(): ItemEntity = ItemEntity(
    id = id,
    name = name,
    description = description,
    imageUrl = imageUrl
)

// Domain to API Model
fun Item.toApiModel(): ItemApiModel = ItemApiModel(
    id = id,
    name = name,
    description = description,
    imageUrl = imageUrl
)
```

---

## 🎨 Presentation Layer

### State Management with StateFlow

#### UI State

```kotlin
sealed interface ItemsUiState {
    data object Loading : ItemsUiState
    data class Success(val items: List<Item>) : ItemsUiState
    data class Error(val message: String) : ItemsUiState

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
class ItemsViewModel(
    private val itemRepository: ItemRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<ItemsUiState>(ItemsUiState.Loading)
    val uiState: StateFlow<ItemsUiState> = _uiState.asStateFlow()

    private val _uiEffect = MutableSharedFlow<ItemsUiEffect>()
    val uiEffect: SharedFlow<ItemsUiEffect> = _uiEffect.asSharedFlow()

    init {
        loadItems()
    }

    fun loadItems() {
        viewModelScope.launch {
            _uiState.value = ItemsUiState.Loading

            itemRepository.getItems()
                .onEach { result ->
                    when (result) {
                        is Result.Success -> {
                            if (result.data.isEmpty()) {
                                _uiState.value = ItemsUiState.Error("No items found")
                            } else {
                                _uiState.value = ItemsUiState.Success(result.data)
                            }
                        }
                        is Result.Error -> {
                            _uiState.value = ItemsUiState.Error(
                                result.exception.message ?: "Unknown error"
                            )
                        }
                    }
                }
                .launchIn(viewModelScope)
        }
    }

    fun onItemClick(item: Item) {
        viewModelScope.launch {
            _uiEffect.emit(ItemsUiEffect.NavigateToItemDetail(item.id))
        }
    }

    fun refreshItems() {
        viewModelScope.launch {
            itemRepository.refreshItems()
        }
    }
}
```

#### UI Effect

```kotlin
sealed interface ItemsUiEffect {
    data class NavigateToItemDetail(val itemId: String) : ItemsUiEffect
    data class ShowToast(val message: String) : ItemsUiEffect
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
fun ItemsScreen(
    viewModel: ItemsViewModel = koinViewModel(),
    onItemClick: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    // Handle side effects
    LaunchedEffect(Unit) {
        viewModel.uiEffect.collect { effect ->
            when (effect) {
                is ItemsUiEffect.NavigateToItemDetail -> {
                    onItemClick(effect.itemId)
                }
                is ItemsUiEffect.ShowToast -> {
                    Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    ItemsContent(
        uiState = uiState,
        onRefresh = { viewModel.refreshItems() },
        onItemClick = { item -> viewModel.onItemClick(item) }
    )
}

```

### UI Content

```kotlin
@Composable
fun ItemsContent(
    uiState: ItemsUiState,
    onRefresh: () -> Unit,
    onItemClick: (Item) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Items") },
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
                is ItemsUiState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                is ItemsUiState.Success -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(uiState.items) { item ->
                            ItemItem(
                                item = item,
                                onClick = { onItemClick(item) }
                            )
                        }
                    }
                }
                is ItemsUiState.Error -> {
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

### Item Item

```kotlin
@Composable
fun ItemItem(
    item: Item,
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
            // Item image
            AsyncImage(
                model = item.imageUrl,
                contentDescription = "${item.name} image",
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape),
                placeholder = painterResource(R.drawable.ic_item_placeholder)
            )

            Spacer(modifier = Modifier.width(16.dp))

            // Item info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = item.description,
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

    single { get<AppDatabase>().itemDao() }
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

    single { get<Retrofit>().create(ItemApiService::class.java) }
}
```

### Repository Module

```kotlin
val repositoryModule = module {
    single<ItemRepository> { ItemRepositoryImpl(get(), get()) }
}
```

### ViewModel Module

```kotlin
val viewModelModule = module {
    viewModel { ItemsViewModel(get()) }
    viewModel { (itemId: String) -> ItemDetailViewModel(itemId, get()) }
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
class ItemsViewModelTest {

    @Test
    fun `loadItems emits loading then success`() = runTest {
        // Given
        val fakeItems = listOf(Item("1", "Item Name", "Item Description", null))
        val repository = FakeItemRepository(fakeItems)
        val viewModel = ItemsViewModel(repository)

        // When
        viewModel.loadItems()

        // Then
        assertEquals(ItemsUiState.Loading, viewModel.uiState.value)

        advanceUntilIdle()

        assertEquals(
            ItemsUiState.Success(fakeItems),
            viewModel.uiState.value
        )
    }
}
```

### UI Testing

```kotlin
class ItemsScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun loading_showsProgressIndicator() {
        composeTestRule.setContent {
            ItemsContent(
                uiState = ItemsUiState.Loading,
                onRefresh = {},
                onItemClick = {}
            )
        }

        composeTestRule.onNodeWithTag("loading_indicator")
            .assertIsDisplayed()
    }
}
```

---

## 📁 Project Structure

### Modified Architecture with Common and Business-Specific Directories

The project follows a modified architecture where common components are separated from business-specific components:

```
app/src/main/
├── java/com/example/test/
│   ├── App.kt
│   ├── common/
│   │   ├── data/
│   │   │   ├── api/
│   │   │   │   └── [API models, services, interceptors]
│   │   │   ├── local/
│   │   │   │   ├── database/
│   │   │   │   │   └── AppDatabase.kt
│   │   │   │   ├── dao/
│   │   │   │   │   └── [DAO interfaces]
│   │   │   │   └── entity/
│   │   │   │       └── [Room entities]
│   │   │   └── repository/
│   │   │       └── [Common repository interfaces and implementations]
│   │   ├── domain/
│   │   │   └── model/
│   │   │       └── [Common domain models]
│   │   └── ui/
│   │       └── theme/
│   │           └── [Common UI themes and styling]
│   ├── mainbiz/
│   │   ├── MainActivity.kt
│   │   └── presentation/
│   │       └── [Main business screen ViewModels and UI state]
│   ├── [otherbiz]/
│   │   └── presentation/
│   │       └── [Other business screen components]
│   ├── di/
│   │   ├── DatabaseModule.kt
│   │   ├── NetworkModule.kt
│   │   ├── RepositoryModule.kt
│   │   └── ViewModelModule.kt
│   └── util/
│       └── [Common utilities]
└── res/
    ├── drawable/
    ├── values/
    │   ├── strings.xml
    │   ├── colors.xml
    │   └── themes.xml
    └── mipmap/
```

### Architecture Decisions

1. **Common Directory (`common/`)**:
   - Contains shared components used across multiple business modules
   - Includes common data models, API services, local storage, domain models, and UI themes
   - Promotes code reuse and consistency

2. **Business-Specific Directories (`mainbiz/`, `[otherbiz]/`)**:
   - Main screen related components (activities, presentations) in `mainbiz/`
   - Other business modules in separate directories following the same pattern
   - Each business module contains its own activities, ViewModels, UI components, and specific data/models

3. **Separation of Concerns**:
   - Common components are isolated for cross-module use
   - Business-specific logic and UI are encapsulated in their respective modules
   - Clear boundaries between shared and specific functionality

4. **Scalability**:
   - New business features can be added as separate directories
   - Common components can be evolved independently
   - Team collaboration is facilitated by clear module boundaries

### Benefits of This Architecture

- **Maintainability**: Clear separation between common and business-specific code
- **Reusability**: Common components are easily accessible across modules
- **Team Development**: Different teams can work on different business modules
- **Testing**: Each module can be tested independently
- **Performance**: Only relevant modules are loaded for specific features

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
