package com.example.sdk;

import android.os.Bundle;

interface ISkillRequest {
    void cancel(String reason);
    void inform(boolean isComplete, in Bundle info);
}
