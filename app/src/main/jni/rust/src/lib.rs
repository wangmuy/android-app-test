//extern crate jni;
use jni::JNIEnv;
use jni::objects::{JClass, JString};
use jni::sys::{jint, jstring};
use std::error::Error;
use wasmtime::*;

#[no_mangle]
pub extern "C" fn Java_com_example_test_RustLib_hello(env: JNIEnv, class: JClass, input: JString) -> jstring {
  let input: String = env.get_string(input).expect("could not get java string!").into();
  let output = env.new_string(format!("Hello, {}!", input)).expect("could not create java string!");
  output.into_inner()
}

#[no_mangle]
pub extern "C" fn Java_com_example_test_RustLib_callWasm(env: JNIEnv, class: JClass, absFilePath: JString) -> jint {
  let wasmFilePath: String = env.get_string(absFilePath).expect("could not get java string!").into();
  let engine = Engine::default();
  let module = Module::from_file(&engine, wasmFilePath).unwrap();
  let mut store = Store::new(&engine, ());
  let instance = Instance::new(&mut store, &module, &[]).unwrap();
  let answer = instance.get_func(&mut store, "answer").expect("'answer' was not an exported function");
  let answer = answer.typed::<(), i32, _>(&store).unwrap();
  let result = answer.call(&mut store, ()).unwrap();
  return result;
}
