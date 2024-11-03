#/bin/bash

# 执行顺序：
# 设置代理
# ./build.sh v4.0.0.1 --docker-hub   
# 取消代理
# ./build.sh v4.0.0.1 --huawei
# 如果推送到 huawei 仓库时失败，可尝试手动执行对应分支下的指令

if [ "--huawei" = $2 ]
then
  echo 构建 docker image
  cd kuboard-server

  VERSION=$1

  # export VERSION=v4.0.0.1
  # 手动执行下面的指令

  docker buildx build --platform linux/amd64 --build-arg VITE_KUBOARD_VERSION=$VERSION \
    -t swr.cn-east-2.myhuaweicloud.com/kuboard/kuboard-v4-ldap-example:$VERSION-amd64 -t swr.cn-east-2.myhuaweicloud.com/kuboard/kuboard-v4-ldap-example:v4-amd64 . --load
  docker buildx build --platform linux/arm64 --build-arg VITE_KUBOARD_VERSION=$VERSION \
    -t swr.cn-east-2.myhuaweicloud.com/kuboard/kuboard-v4-ldap-example:$VERSION-arm64 -t swr.cn-east-2.myhuaweicloud.com/kuboard/kuboard-v4-ldap-example:v4-arm64 . --load
  
  docker push swr.cn-east-2.myhuaweicloud.com/kuboard/kuboard-v4-ldap-example:$VERSION-amd64
  docker push swr.cn-east-2.myhuaweicloud.com/kuboard/kuboard-v4-ldap-example:$VERSION-arm64

  docker run --rm \
    mplatform/manifest-tool:v2.1.6 \
    --debug --username cn-east-2@${SWR_AK} --password ${SWR_PW} \
    push from-args \
    --platforms linux/amd64,linux/arm64 \
    --template swr.cn-east-2.myhuaweicloud.com/kuboard/kuboard-v4-ldap-example:$VERSION-ARCH \
    --target swr.cn-east-2.myhuaweicloud.com/kuboard/kuboard-v4-ldap-example:$VERSION

  docker run --rm \
    mplatform/manifest-tool:v2.1.6 \
    --debug --username cn-east-2@${SWR_AK} --password ${SWR_PW} \
    push from-args \
    --platforms linux/amd64,linux/arm64 \
    --template swr.cn-east-2.myhuaweicloud.com/kuboard/kuboard-v4-ldap-example:$VERSION-ARCH \
    --target swr.cn-east-2.myhuaweicloud.com/kuboard/kuboard-v4-ldap-example:v4
else 
  if [ "--docker-hub" = $2 ]
  then
    echo 构建 docker image
    cd kuboard-server

    docker buildx build --platform linux/amd64,linux/arm64 --build-arg VITE_KUBOARD_VERSION=$1 \
      -t eipwork/kuboard-v4-ldap-example:$1 -t eipwork/kuboard-v4-ldap-example:v4 --push .
  else

    echo 清理 kuboard-server/public
    rm -rf ./kuboard-server/public

    export VITE_KUBOARD_VERSION=$1

    echo 构建 kb-portal
    cd kb-portal
    pnpm build:pro

    cd ..

    mv ./kb-portal/dist ./kuboard-server/public

    echo 构建 kuboard-server
    mvn -DskipTests=true -Drevision=$1 clean package

    echo 构建 docker image
    cd kuboard-server
    docker build --build-arg VITE_KUBOARD_VERSION=$1 -t eipwork/kuboard:$1 .
  fi
fi



