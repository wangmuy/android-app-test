package com.example.test;

public class QueryState<T> {
    public final boolean inProgress;
    public final boolean success;
    public final Throwable error;
    public final T value;

    private QueryState(boolean inProgress, boolean success, Throwable error, T value) {
        this.inProgress = inProgress;
        this.success = success;
        this.error = error;
        this.value = value;
    }

    @Override
    public String toString() {
        return QueryState.class.getSimpleName() + "[inProgress="+inProgress
                +", success="+success + ", error="+error + ", value="+value;
    }

    private static final QueryState<?> INPROGRESS = new QueryState<>(true, false, null, null);
    public static <T> QueryState<T> inProgress() {
        return (QueryState<T>) INPROGRESS;
    }

    public static <T> QueryState<T> success(T value) {
        return new QueryState<>(false, true, null, value);
    }

    public static <T> QueryState<T> error(Throwable t) {
        return new QueryState<>(false, false, t, null);
    }
}
