#/bin/bash

export KUBOARD_LDAP_EXAMPLE_VERSION=$1


# echo 构建 kuboard-v4-ldap-example
# mvn -DskipTests=true -Drevision=$1 clean package


echo 构建 docker image

if [ "--push" = $2 ]
then
  docker buildx build --platform linux/amd64,linux/arm64 --build-arg KUBOARD_LDAP_EXAMPLE_VERSION=$1 \
    -t eipwork/kuboard-v4-ldap-example:$1 -t eipwork/kuboard-v4-ldap-example:v4 --push .
else
  docker build --build-arg KUBOARD_LDAP_EXAMPLE_VERSION=$1 -t eipwork/kuboard-v4-ldap-example:$1 .
fi


