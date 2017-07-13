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

package com.example.test.tasks.viewmodel.usecase;

import android.support.annotation.NonNull;


import com.example.test.UseCase;
import com.example.test.data.source.TasksRepository;
import com.example.test.utils.schedulers.BaseSchedulerProvider;

import rx.Observable;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Deletes tasks marked as completed.
 */
public class ClearCompleteTasks
        extends UseCase<ClearCompleteTasks.RequestValues, ClearCompleteTasks.ResponseValue> {

    private final TasksRepository mTasksRepository;

    @NonNull
    private final BaseSchedulerProvider mSchedulerProvider;

    public ClearCompleteTasks(@NonNull TasksRepository tasksRepository,
                              @NonNull BaseSchedulerProvider schedulerProvider) {
        mTasksRepository = checkNotNull(tasksRepository, "tasksRepository cannot be null!");
        mSchedulerProvider = checkNotNull(schedulerProvider, "schedulerProvider cannot be null");
    }

    @Override
    protected Observable<ResponseValue> executeUseCase(final RequestValues values) {
        return Observable.just(null).map((o) -> {
            mTasksRepository.clearCompletedTasks();
            return new ResponseValue();
        }).subscribeOn(mSchedulerProvider.io());
    }

    public static class RequestValues implements UseCase.RequestValues { }

    public static class ResponseValue implements UseCase.ResponseValue { }
}
