/*
 * Copyright 2020-present HiveMQ GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hivemq.extensions.tls.auth;

import com.hivemq.extension.sdk.api.auth.SimpleAuthenticator;
import com.hivemq.extension.sdk.api.auth.parameter.SimpleAuthInput;
import com.hivemq.extension.sdk.api.auth.parameter.SimpleAuthOutput;
import com.hivemq.extension.sdk.api.auth.parameter.TopicPermission;
import com.hivemq.extension.sdk.api.client.parameter.TlsInformation;
import com.hivemq.extension.sdk.api.services.builder.Builders;

import java.security.cert.X509Certificate;
import java.util.Optional;
import java.util.logging.Logger;

public class MyAuthenticator implements SimpleAuthenticator {

    Logger logger = Logger.getLogger(MyAuthenticator.class.toString());

    @Override
    public void onConnect(SimpleAuthInput simpleAuthInput, SimpleAuthOutput simpleAuthOutput) {
        logger.info("Called My Authenticator for Authentication");
        logger.info("SimpleAuthInput::"+simpleAuthInput);
        Optional<TlsInformation> tlsInformation = simpleAuthInput.getConnectionInformation().getTlsInformation();
        if (tlsInformation.isEmpty()) {
            simpleAuthOutput.failAuthentication();
        }
        if (tlsInformation.isPresent()) {
            X509Certificate certificate = tlsInformation.get().getCertificate();
            logger.info("Client Connected with certificate::" + certificate);

            TopicPermission permission = Builders.topicPermission()
                    .topicFilter("test/#")
                    .build();


            //add this permission to the client's default permissions
            simpleAuthOutput.getDefaultPermissions().add(permission);

            simpleAuthOutput.authenticateSuccessfully();
        } else {
            logger.info("tls information is not present. Considering Auth failed");
            simpleAuthOutput.failAuthentication();
        }
    }
}