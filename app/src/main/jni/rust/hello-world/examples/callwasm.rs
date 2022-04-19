use std::error::Error;
use wasmtime::*;

fn main() -> Result<(), Box<dyn Error>> {
  let engine = Engine::default();
  let module = Module::from_file(&engine, "target/wasm32-wasi/release/hello_world.wasm").unwrap();
  let mut store = Store::new(&engine, ());
  let instance = Instance::new(&mut store, &module, &[]).unwrap();
  let answer = instance.get_func(&mut store, "answer").expect("'answer' was not an exported function");
  let answer = answer.typed::<(), i32, _>(&store).unwrap();
  let result = answer.call(&mut store, ()).unwrap();
  println!("Answer: {:?}", result);
  Ok(())
}