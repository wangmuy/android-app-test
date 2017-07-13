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
import com.example.test.tasks.model.Task;
import com.example.test.tasks.viewmodel.filter.TasksFilterType;
import com.example.test.tasks.viewmodel.filter.FilterFactory;
import com.example.test.tasks.viewmodel.filter.TaskFilter;
import com.example.test.utils.schedulers.BaseSchedulerProvider;

import java.util.List;

import rx.Observable;
import rx.Subscription;
import rx.functions.Func1;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Fetches the list of tasks.
 */
public class GetTasks extends UseCase<GetTasks.RequestValues, GetTasks.ResponseValue> {

    private final TasksRepository mTasksRepository;

    private final FilterFactory mFilterFactory;

    @NonNull
    private final BaseSchedulerProvider mSchedulerProvider;

    public GetTasks(@NonNull TasksRepository tasksRepository,
                    @NonNull FilterFactory filterFactory,
                    @NonNull BaseSchedulerProvider schedulerProvider) {
        mTasksRepository = checkNotNull(tasksRepository, "tasksRepository cannot be null!");
        mFilterFactory = checkNotNull(filterFactory, "filterFactory cannot be null!");
        mSchedulerProvider = checkNotNull(schedulerProvider, "schedulerProvider cannot be null");
    }

    @Override
    protected Observable<ResponseValue> executeUseCase(final RequestValues values) {
        if (values.isForceUpdate()) {
            mTasksRepository.refreshTasks();
        }

        TasksFilterType currentFiltering = values.getCurrentFiltering();
        TaskFilter taskFilter = mFilterFactory.create(currentFiltering);
        Observable<List<Task>> list = mTasksRepository
                .getTasks()
                .flatMap(new Func1<List<Task>, Observable<Task>>() {
                    @Override
                    public Observable<Task> call(List<Task> tasks) {
                        return Observable.from(tasks);
                    }
                })
                .filter(taskFilter::accept)
                .toList()
                .subscribeOn(mSchedulerProvider.computation())
                .observeOn(mSchedulerProvider.ui());
//                .subscribe(
//                        // onNext
//                        (List<Task> tasks) -> {
//                            ResponseValue responseValue = new ResponseValue(tasks);
//                            getUseCaseCallback().onSuccess(responseValue);
//                        },
//                        // onError
//                        throwable -> getUseCaseCallback().onError(),
//                        // onCompleted
//                        () -> getUseCaseCallback().onCompleted()
//                );
        return list.map((l) -> new ResponseValue(l));

    }


    public static final class RequestValues implements UseCase.RequestValues {

        private final TasksFilterType mCurrentFiltering;
        private final boolean mForceUpdate;

        public RequestValues(boolean forceUpdate, @NonNull TasksFilterType currentFiltering) {
            mForceUpdate = forceUpdate;
            mCurrentFiltering = checkNotNull(currentFiltering, "currentFiltering cannot be null!");
        }

        public boolean isForceUpdate() {
            return mForceUpdate;
        }

        public TasksFilterType getCurrentFiltering() {
            return mCurrentFiltering;
        }
    }

    public static final class ResponseValue implements UseCase.ResponseValue {

        private final List<Task> mTasks;

        public ResponseValue(@NonNull List<Task> tasks) {
            mTasks = checkNotNull(tasks, "tasks cannot be null!");
        }

        public List<Task> getTasks() {
            return mTasks;
        }
    }
}
