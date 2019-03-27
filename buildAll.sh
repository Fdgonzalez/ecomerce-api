./mvnw package

#Discovery
cd discovery-service
mkdir -p target/dependency && (cd target/dependency; jar -xf ../*.jar)
#docker build -t ecomerce/discovery .
cd ..

#Auth
cd auth-service
mkdir -p target/dependency && (cd target/dependency; jar -xf ../*.jar)
#docker build -t ecomerce/auth .
cd ..

#Cart
cd cart-service
mkdir -p target/dependency && (cd target/dependency; jar -xf ../*.jar)
#docker build -t ecomerce/cart .
cd ..

#Config
cd config-service
mkdir -p target/dependency && (cd target/dependency; jar -xf ../*.jar)
#docker build -t ecomerce/config .
cd ..

#Gateway
cd gateway-service
mkdir -p target/dependency && (cd target/dependency; jar -xf ../*.jar)
#docker build -t ecomerce/gateway .
cd ..

#orders
cd orders-service
mkdir -p target/dependency && (cd target/dependency; jar -xf ../*.jar)
#docker build -t ecomerce/orders .
cd ..

#Product
cd product-service
mkdir -p target/dependency && (cd target/dependency; jar -xf ../*.jar)
#docker build -t ecomerce/product .
cd ..

#Stock
cd stock-service
mkdir -p target/dependency && (cd target/dependency; jar -xf ../*.jar)
#docker build -t ecomerce/stock .
cd ..

#User
cd user-service
mkdir -p target/dependency && (cd target/dependency; jar -xf ../*.jar)
#docker build -t ecomerce/user .
cd ..
