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

import com.example.test.BasePresenter;
import com.example.test.BaseView;
import com.example.test.QueryState;
import com.example.test.tasks.model.Task;
import com.example.test.tasks.viewmodel.filter.TasksFilterType;

import java.util.List;

import rx.Completable;
import rx.Observable;

/**
 * This specifies the contract between the view and the presenter.
 */
public interface TasksContract {

    interface View extends BaseView<Presenter> {

        void showAddTask();

        void showTaskDetailsUi(String taskId);
    }

    interface Presenter extends BasePresenter {

        Observable<QueryState<List<Task>>> getTaskListStateObservable();

        Observable<List<Task>> loadTasks(boolean forceUpdate);

        void addNewTask();

        void openTaskDetails(@NonNull Task requestedTask);

        Completable completeTask(@NonNull Task completedTask);

        Completable activateTask(@NonNull Task activeTask);

        Completable clearCompletedTasks();

        void setFiltering(TasksFilterType requestType);

        TasksFilterType getFiltering();
    }
}
