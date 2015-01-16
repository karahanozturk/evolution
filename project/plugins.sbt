resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"

resolvers += Resolver.file("Local repo", file(System.getProperty("user.home") + "/.ivy2/local"))(Resolver.ivyStylePatterns)

// The Play plugin
addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.3.6")
