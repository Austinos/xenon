/*
 * Copyright (c) 2014-2018 VMware, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, without warranties or
 * conditions of any kind, EITHER EXPRESS OR IMPLIED.  See the License for the
 * specific language governing permissions and limitations under the License.
 */

package com.vmware.xenon.common;

/**
 * Sends {@link Operation}s.
 *
 * @see ServiceClient
 * @see ServiceHost
 * @see Service
 */
public interface ServiceRequestSender {

    /**
     * Sends an asynchronous request.
     *
     * @param op The operation to dispatch. The Operation should not be mutated after submission.
     */
    void sendRequest(Operation op);

    /**
     * Sends an asynchronous request and returns the eventual response body as deferred result.
     * @param op The request to send.
     * @param resultType The expected type of the response body.
     * @return Deferred result with the resultType state of the response body or
     * <code>null</code> if body is empty.
     */
    default <T> DeferredResult<T> sendWithDeferredResult(Operation op, Class<T> resultType) {
        return sendWithDeferredResult(op)
                .thenApply(response -> response.getBodyRaw() == null
                        ? null
                        : response.getBody(resultType)
                );
    }

    /**
     * Sends an asynchronous request and returns the eventual response as deferred result.
     * @param op The request to send.
     * @return
     */
    default DeferredResult<Operation> sendWithDeferredResult(Operation op) {
        DeferredResult<Operation> deferred = new DeferredResult<Operation>();
        op.nestCompletion((response, e) -> {
            if (e != null) {
                deferred.fail(e);
            } else {
                deferred.complete(response);
            }
        });
        sendRequest(op);
        return deferred;
    }
}
