use std::error::Error;
use wasmer::{Store, Module, Instance, Value, imports};

fn main() -> Result<(), Box<dyn Error>> {
  let store = Store::default();
  let module = Module::from_file(&store, "target/wasm32-wasi/release/hello_world.wasm").unwrap();
  let import_object = imports! {};
  let instance = Instance::new(&module, &import_object).unwrap();
  let answer = instance.exports.get_function("answer").unwrap();
  let result = answer.call(&[]).unwrap()[0].unwrap_i32();
  println!("Answer: {:?}", result);
  Ok(())
}
