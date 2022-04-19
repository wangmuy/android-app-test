//extern crate jni;
use jni::JNIEnv;
use jni::objects::{JClass, JString};
use jni::sys::{jint, jstring};
use wasmer::{Store, Module, Instance, Value, imports};

#[no_mangle]
pub extern "C" fn Java_com_example_test_RustLib_hello(env: JNIEnv, class: JClass, input: JString) -> jstring {
  let input: String = env.get_string(input).expect("could not get java string!").into();
  let output = env.new_string(format!("Hello, {}!", input)).expect("could not create java string!");
  output.into_inner()
}

#[no_mangle]
pub extern "C" fn Java_com_example_test_RustLib_callWasm(env: JNIEnv, class: JClass, absFilePath: JString) -> jint {
  let wasmFilePath: String = env.get_string(absFilePath).expect("could not get java string!").into();
  let store = Store::default();
  let module = Module::from_file(&store, wasmFilePath).unwrap();
  let import_object = imports! {};
  let instance = Instance::new(&module, &import_object).unwrap();
  let answer = instance.exports.get_function("answer").unwrap();
  let result = answer.call(&[]).unwrap()[0].unwrap_i32();
  return result;
}
