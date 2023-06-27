# App structure
* :base

* :feat1:api
* :feat1:impl depends on api(:feat1:api), impl(:base)
* :feat1:di depends on api(:feat1:api), impl(:feat1:impl)

* :feat2:api
* :feat2:impl depends on api(:feat2:api), impl(:base), impl(:feat1:api)
  * such that :feat2:impl cannot use :feat1:impl
* :feat2:di depends on api(:feat2:api), impl(:feat1:api), impl(:feat2:impl)
  * such that :feat2:di cannot use :feat1:impl

* :feat3:api
* :feat3:impl as composite build. depends on compileOnly(:feat3:api)
* :feat3:di depends on api(:feat3:api), aar("com.wangmuy:implaar") aka groupName=com.wangmuy artifactName=implaar

* :app depends on api(:base), impl(:feat1:di), impl(:feat2:di), impl(:feat3:di)
  * such that :app cannot use :feat1:impl, :feat2:impl and feat3:impl
