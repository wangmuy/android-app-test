/*
 * Copyright (C) 2015 The Android Open Source Project
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

package com.example.test;

import static com.google.common.base.Preconditions.checkNotNull;

import android.content.Context;
import android.support.annotation.NonNull;

import com.example.test.data.source.TasksDataSource;
import com.example.test.data.source.TasksRepository;
import com.example.test.data.source.local.TasksLocalDataSource;
import com.example.test.data.source.remote.TasksRemoteDataSource;
import com.example.test.tasks.viewmodel.filter.FilterFactory;
import com.example.test.tasks.viewmodel.usecase.ActivateTask;
import com.example.test.tasks.viewmodel.usecase.ClearCompleteTasks;
import com.example.test.tasks.viewmodel.usecase.CompleteTask;
import com.example.test.tasks.viewmodel.usecase.GetTasks;
import com.example.test.utils.schedulers.SchedulerProvider;


/**
 * Enables injection of production implementations for
 * {@link TasksDataSource} at compile time.
 */
public class Injection {

    public static TasksRepository provideTasksRepository(@NonNull Context context) {
        checkNotNull(context);
        return TasksRepository.getInstance(TasksRemoteDataSource.getInstance(),
                TasksLocalDataSource.getInstance(context, provideSchedulerProvider()));
    }

    public static GetTasks provideGetTasks(@NonNull Context context) {
        return new GetTasks(provideTasksRepository(context), new FilterFactory(), provideSchedulerProvider());
    }

    public static SchedulerProvider provideSchedulerProvider() {
        return SchedulerProvider.getInstance();
    }

//    public static UseCaseHandler provideUseCaseHandler() {
//        return UseCaseHandler.getInstance();
//    }

//    public static GetTask provideGetTask(Context context) {
//        return new GetTask(Injection.provideTasksRepository(context));
//    }

//    public static SaveTask provideSaveTask(Context context) {
//        return new SaveTask(Injection.provideTasksRepository(context));
//    }

    public static CompleteTask provideCompleteTasks(Context context) {
        return new CompleteTask(Injection.provideTasksRepository(context), provideSchedulerProvider());
    }

    public static ActivateTask provideActivateTask(Context context) {
        return new ActivateTask(Injection.provideTasksRepository(context), provideSchedulerProvider());
    }

    public static ClearCompleteTasks provideClearCompleteTasks(Context context) {
        return new ClearCompleteTasks(Injection.provideTasksRepository(context), provideSchedulerProvider());
    }

//    public static DeleteTask provideDeleteTask(Context context) {
//        return new DeleteTask(Injection.provideTasksRepository(context));
//    }
//
//    public static GetStatistics provideGetStatistics(Context context) {
//        return new GetStatistics(Injection.provideTasksRepository(context));
//    }
}
