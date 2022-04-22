//extern crate jni;
use jni::JNIEnv;
use jni::objects::{JClass, JString};
use jni::sys::{jint, jstring};

use std::ptr;
use std::ffi::CString;
mod wasmedge;
use wasmedge::*;

#[no_mangle]
pub extern "C" fn Java_com_example_test_RustLib_hello(env: JNIEnv, class: JClass, input: JString) -> jstring {
  let input: String = env.get_string(input).expect("could not get java string!").into();
  let output = env.new_string(format!("Hello, {}!", input)).expect("could not create java string!");
  output.into_inner()
}

#[no_mangle]
pub extern "C" fn Java_com_example_test_RustLib_callWasm(env: JNIEnv, class: JClass, absFilePath: JString) -> jint {
  let wasmFilePath: String = env.get_string(absFilePath).expect("could not get java string!").into();
  unsafe {
    let mut conf = WasmEdge_ConfigureCreate();
    WasmEdge_ConfigureAddHostRegistration(conf, WasmEdge_HostRegistration_Wasi);
    let mut vm_ctx = WasmEdge_VMCreate(conf, ptr::null_mut());
    let func_name = WasmEdge_StringCreateByCString(CString::new("answer").unwrap().as_ptr());
    let mut ret_val = [WasmEdge_Value{Type: WasmEdge_ValType_I32, Value: 0}; 1];
    let res = WasmEdge_VMRunWasmFromFile(
      vm_ctx, wasmFilePath.as_str().as_ptr(),
      func_name, ptr::null_mut(), 0, ret_val.as_mut_ptr(), 1);
    WasmEdge_VMDelete(vm_ctx);
    WasmEdge_ConfigureDelete(conf);
    WasmEdge_StringDelete(func_name);
    match WasmEdge_ResultOK(res) {
      true => WasmEdge_ValueGetI32(ret_val[0]),
      false => panic!("callWasm failed!")
    }
  }
}
