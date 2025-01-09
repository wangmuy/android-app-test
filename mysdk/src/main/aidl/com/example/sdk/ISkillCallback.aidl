package com.example.sdk;

import android.os.Bundle;
import com.example.sdk.ISkillRequest;

interface ISkillCallback {
    void onProgress(ISkillRequest request, int progress, int max);
    void onStatusChanged(ISkillRequest request, int statusCode);
    void notify(ISkillRequest request, int resultCode, in Bundle bundle);
}
