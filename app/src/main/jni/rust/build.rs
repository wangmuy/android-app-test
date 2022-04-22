use std::env;

fn main() {
  println!("cargo:rerun-if-changed=src/lib.rs");
  let dir = env::var("CARGO_MANIFEST_DIR").unwrap();
  let arch = match std::env::var("TARGET").unwrap().as_str() {
    "aarch64-linux-android" => "arm64-v8a",
    "armv7-linux-androideabi" => "armeabi-v7a",
    _ => panic!("need aarch64 or armv7!")
  };
  println!("cargo:rustc-link-search=native={}/../../jniLibs/{}", dir, arch);
  println!("cargo:rustc-link-lib=dylib=wasmedge_c");
}
