//extern crate jni;
use jni::JNIEnv;
use jni::objects::{JClass, JString};
use jni::sys::jstring;

#[no_mangle]
pub extern "C" fn Java_com_example_test_RustLib_hello(env: JNIEnv, class: JClass, input: JString) -> jstring {
  let input: String = env.get_string(input).expect("could not get java string!").into();
  let output = env.new_string(format!("Hello, {}!", input)).expect("could not create java string!");
  output.into_inner()
}
