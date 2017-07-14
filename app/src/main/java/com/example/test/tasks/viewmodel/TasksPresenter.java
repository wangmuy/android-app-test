/*
 * Copyright 2016, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.test.tasks.viewmodel;

import android.support.annotation.NonNull;

import com.example.test.ObservableField;
import com.example.test.QueryState;
import com.example.test.data.source.TasksRepository;
import com.example.test.tasks.model.Task;
import com.example.test.tasks.viewmodel.filter.FilterFactory;
import com.example.test.tasks.viewmodel.filter.TasksFilterType;
import com.example.test.tasks.viewmodel.usecase.ActivateTask;
import com.example.test.tasks.viewmodel.usecase.ClearCompleteTasks;
import com.example.test.tasks.viewmodel.usecase.CompleteTask;
import com.example.test.tasks.viewmodel.usecase.GetTasks;
import com.example.test.utils.schedulers.BaseSchedulerProvider;

import java.util.List;

import rx.Completable;
import rx.Observable;

import static com.google.common.base.Preconditions.checkNotNull;

public class TasksPresenter implements TasksContract.Presenter {

    private final TasksContract.View mTasksView;

    @NonNull
    private final BaseSchedulerProvider mSchedulerProvider;

    @NonNull
    private TasksFilterType mCurrentFiltering = TasksFilterType.ALL_TASKS;

    private boolean mFirstLoad = true;

    private final GetTasks mGetTasks;
    private final CompleteTask mCompleteTask;
    private final ActivateTask mActivateTask;
    private final ClearCompleteTasks mClearCompleteTasks;

    private ObservableField<QueryState<List<Task>>> mTasksQuery = new ObservableField<>(null);

    public TasksPresenter(@NonNull TasksRepository tasksRepository,
                          @NonNull TasksContract.View tasksView,
                          @NonNull BaseSchedulerProvider schedulerProvider) {
        checkNotNull(tasksRepository, "tasksRepository cannot be null");
        mSchedulerProvider = checkNotNull(schedulerProvider, "schedulerProvider cannot be null");
        mTasksView = checkNotNull(tasksView, "tasksView cannot be null!");
        mGetTasks = new GetTasks(tasksRepository, new FilterFactory(), mSchedulerProvider);
        mCompleteTask = new CompleteTask(tasksRepository, schedulerProvider);
        mActivateTask = new ActivateTask(tasksRepository, schedulerProvider);
        mClearCompleteTasks = new ClearCompleteTasks(tasksRepository, schedulerProvider);

        mTasksView.setPresenter(this);
    }

    @Override
    public Observable<QueryState<List<Task>>> getTaskListStateObservable() {
        return mTasksQuery.asObservable();
    }

    @Override
    public Observable<List<Task>> loadTasks(boolean forceUpdate) {
        boolean force = forceUpdate || mFirstLoad;
        mFirstLoad = false;
        return loadTasks(force, true);
    }

    /**
     * @param forceUpdate   Pass in true to refresh the data in the {@link com.example.test.data.source.TasksDataSource}
     * @param showLoadingUI Pass in true to display a loading icon in the UI
     */
    private Observable<List<Task>> loadTasks(final boolean forceUpdate, final boolean showLoadingUI) {
        mGetTasks.setRequestValues(new GetTasks.RequestValues(forceUpdate, mCurrentFiltering));
        return mGetTasks.execute()
                .doOnSubscribe(() -> {
                    mTasksQuery.set(QueryState.inProgress());
                })
                .map(GetTasks.ResponseValue::getTasks)
                .doOnNext(list -> {
                    mTasksQuery.set(QueryState.success(list));
                })
                .doOnError((t) -> mTasksQuery.set(QueryState.error(t)))
                .subscribeOn(mSchedulerProvider.io());
    }

    @Override
    public void addNewTask() {
        mTasksView.showAddTask();
    }

    @Override
    public void openTaskDetails(@NonNull Task requestedTask) {
        checkNotNull(requestedTask, "requestedTask cannot be null!");
        mTasksView.showTaskDetailsUi(requestedTask.getId());
    }

    @Override
    public Completable completeTask(@NonNull Task completedTask) {
        checkNotNull(completedTask, "completedTask cannot be null!");
        return Completable.create(ignored -> {
            mCompleteTask.setRequestValues(new CompleteTask.RequestValues(completedTask));
            mCompleteTask.execute().subscribe();
            loadTasks(false, false).subscribe();
        });
    }

    @Override
    public Completable activateTask(@NonNull Task activeTask) {
        checkNotNull(activeTask, "activeTask cannot be null!");
        return Completable.create(ignored -> {
            mActivateTask.setRequestValues(new ActivateTask.RequestValues(activeTask));
            mActivateTask.execute().subscribe();
            loadTasks(false, false).subscribe();
        });
    }

    @Override
    public Completable clearCompletedTasks() {
        return Completable.create(ignored -> {
            mClearCompleteTasks.setRequestValues(new ClearCompleteTasks.RequestValues());
            mClearCompleteTasks.execute().subscribe();
            loadTasks(false, false).subscribe();
        });
    }

    /**
     * Sets the current task filtering type.
     *
     * @param requestType Can be {@link TasksFilterType#ALL_TASKS},
     *                    {@link TasksFilterType#COMPLETED_TASKS}, or
     *                    {@link TasksFilterType#ACTIVE_TASKS}
     */
    @Override
    public void setFiltering(@NonNull TasksFilterType requestType) {
        mCurrentFiltering = requestType;
    }

    @Override
    public TasksFilterType getFiltering() {
        return mCurrentFiltering;
    }

}
