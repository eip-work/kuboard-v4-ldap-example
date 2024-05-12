# Kuboard v4 Ldap example

This project is a example to show how to authenticate Kuboard v4 via LDAP server.

Kuboard v4 has a Service Provider Interface to authenticate user and load user details info.


## Prerequisit

To run the example, you have to prepare:
* docker engine >= 20.01


## Run the example

* Execute the following command in the source code root directory.

  ```sh
  docker compose up -d
  ```

* Open the following url in your browser

  `http://localhost:8000`

* Login with admin user:

  * username: `admin`
  * password: `Kuboard123`

* Login with Ldap user:

  * username: `user01`
  * password: `password1`

## Build the example

* Execute the following command in the source code root directory.

  ```sh
  docker build -t eipwork/kuboard-v4-ldap-example:v4 .
  ```