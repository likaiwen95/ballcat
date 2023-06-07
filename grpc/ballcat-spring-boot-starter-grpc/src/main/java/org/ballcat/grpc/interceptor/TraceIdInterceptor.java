/*
 * Copyright 2023 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ballcat.grpc.interceptor;

import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;
import org.ballcat.grpc.constant.GrpcConstants;
import org.bson.types.ObjectId;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;

/**
 * 在服务器端，按照拦截器注册的顺序从后到前执行，先执行后面的拦截器，再执行前面的拦截器。
 *
 * @author lingting 2023-04-13 13:23
 */
@Order(GrpcConstants.ORDER_TRACE_ID)
public class TraceIdInterceptor implements ServerInterceptor {

	public static final String TRACE_ID = "traceId";

	private static final Metadata.Key<String> KEY_TRACE_ID = Metadata.Key.of(TRACE_ID,
			Metadata.ASCII_STRING_MARSHALLER);

	@Override
	public <S, R> ServerCall.Listener<S> interceptCall(ServerCall<S, R> call, Metadata headers,
			ServerCallHandler<S, R> next) {
		String traceId = traceId();
		MDC.put(TRACE_ID, traceId);
		try {
			// 返回traceId
			headers.put(KEY_TRACE_ID, traceId);
			return next.startCall(call, headers);
		}
		finally {
			MDC.remove(TRACE_ID);
		}
	}

	protected String traceId() {
		return ObjectId.get().toString();
	}

}
