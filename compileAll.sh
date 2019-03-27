./mvnw package

#Unpack jars to later be used by docker
#Discovery
cd discovery-service
mkdir -p target/dependency && (cd target/dependency; jar -xf ../*.jar)
cd ..

#Admin
cd admin-service
mkdir -p target/dependency && (cd target/dependency; jar -xf ../*.jar)
cd ..

#Auth
cd auth-service
mkdir -p target/dependency && (cd target/dependency; jar -xf ../*.jar)
cd ..

#Cart
cd cart-service
mkdir -p target/dependency && (cd target/dependency; jar -xf ../*.jar)
cd ..

#Config
cd config-service
mkdir -p target/dependency && (cd target/dependency; jar -xf ../*.jar)
cd ..

#Gateway
cd gateway-service
mkdir -p target/dependency && (cd target/dependency; jar -xf ../*.jar)
cd ..

#orders
cd orders-service
mkdir -p target/dependency && (cd target/dependency; jar -xf ../*.jar)
cd ..

#Product
cd product-service
mkdir -p target/dependency && (cd target/dependency; jar -xf ../*.jar)
cd ..

#Stock
cd stock-service
mkdir -p target/dependency && (cd target/dependency; jar -xf ../*.jar)
cd ..

#User
cd user-service
mkdir -p target/dependency && (cd target/dependency; jar -xf ../*.jar)
cd ..
