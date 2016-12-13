# Official tronalddump.io app for Facebook Messenger

[![Apache 2.0 License](https://img.shields.io/:license-apache-blue.svg)](http://www.apache.org/licenses/LICENSE-2.0)


[Tronalddump.io][] is a free api and web archive for the dumbest things Donald Trump has ever said ...

## Deployment

First make sure to store the encrypted tokens and app secret as we don't want to store them GitHub.

    ./gradlew addCredentials --key tronalddumpIoVerifyToken --value the-verify-token
    ./gradlew addCredentials --key tronalddumpIoAccessToken --value the-access-token
    ./gradlew addCredentials --key tronalddumpIoAppSecret --value the-app-secret

After the tokens and app secret we can deploy the app to Google App Engine.

    ./gradlew clean appengineUpdate

## License

This software is released under version 2.0 of the [Apache License][].


[Apache License]: http://www.apache.org/licenses/LICENSE-2.0
[Tronalddump.io]: https://www.tronalddump.io/
