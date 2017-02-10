package com.example.test;

import android.animation.Keyframe;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.view.animation.Interpolator;

public class KeyframeAnimatorHelper {

    public static abstract class KeyframeSentinel {
        protected long duration;
        protected Interpolator interpolator;

        private KeyframeSentinel(long d, Interpolator i) {
            duration = d;
            interpolator = i;
        }

        public KeyframeSentinel setInterpolator(Interpolator i) {
            interpolator = i;
            return this;
        }

        public static KeyframeSentinel ofFloat(float toValue, long duration, Interpolator interpolator) {
            return new KeyframeSentinelFloat(toValue, duration, interpolator);
        }

        public static KeyframeSentinel ofFloatOrigin() {
            return new KeyframeSentinelFloat(0f, 0, null);
        }

        public static KeyframeSentinel ofInt(int toValue, long duration, Interpolator interpolator) {
            return new KeyframeSentinelInt(toValue, duration, interpolator);
        }

        public static KeyframeSentinel ofIntOrigin() {
            return new KeyframeSentinelInt(0, 0, null);
        }

        protected abstract Keyframe getKeyframe(long elapsedTime, long totalTime);
    }

    private static class KeyframeSentinelFloat extends KeyframeSentinel {
        private float toValue;

        public KeyframeSentinelFloat(float v, long d, Interpolator i) {
            super(d, i);
            toValue = v;
        }

        protected Keyframe getKeyframe(long elapsedTime, long totalTime) {
            Keyframe kf = Keyframe.ofFloat(totalTime != 0? (elapsedTime+duration)/(float)totalTime : 0f, toValue);
            kf.setInterpolator(interpolator);
            return kf;
        }
    }

    private static class KeyframeSentinelInt extends KeyframeSentinel {
        private int toValue;

        public KeyframeSentinelInt(int v, long d, Interpolator i) {
            super(d, i);
            toValue = v;
        }

        protected Keyframe getKeyframe(long elapsedTime, long totalTime) {
            Keyframe kf = Keyframe.ofInt(totalTime != 0? (elapsedTime+duration)/totalTime : 0, toValue);
            kf.setInterpolator(interpolator);
            return kf;
        }
    }

    public static ObjectAnimator ofKeyframe(Object target, String propertyName, KeyframeSentinel... sentinels) {
        Keyframe[] frames = new Keyframe[sentinels.length];
        long totalTime = 0;
        for(int i=0; i < sentinels.length; ++i) {
            totalTime += sentinels[i].duration;
        }

        long elapsedTime = 0;
        for(int i=0; i < sentinels.length; ++i) {
            frames[i] = sentinels[i].getKeyframe(elapsedTime, totalTime);
            elapsedTime += sentinels[i].duration;
        }
        PropertyValuesHolder pvh = PropertyValuesHolder.ofKeyframe(propertyName, frames);
        ObjectAnimator anim = ObjectAnimator.ofPropertyValuesHolder(target, pvh).setDuration(totalTime);
        return anim;
    }
}
