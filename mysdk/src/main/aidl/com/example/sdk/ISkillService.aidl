package com.example.sdk;

import com.example.sdk.ISkillRequest;
import com.example.sdk.ISkillCallback;

interface ISkillService {
    ISkillRequest call(String funcId, String cmd, in Bundle bundle, ISkillCallback callback);
}