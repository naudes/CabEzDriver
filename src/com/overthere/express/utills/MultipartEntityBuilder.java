package com.overthere.express.utills;

/**
 * Created by Jack on 25/10/2016.
 */

 /*
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * .
 *
 */

    import java.nio.charset.Charset;
    import java.util.List;
    import org.apache.http.HttpEntity;
    import org.apache.http.entity.ContentType;
    import org.apache.http.entity.mime.HttpMultipartMode;
    import org.apache.http.util.Args;

    /**
     * Builder for multipart {@link HttpEntity}s.
     *
     * @since 4.3
     */
    public class MultipartEntityBuilder {

        /**
         * The pool of ASCII chars to be used for generating a multipart boundary.
         */
        private final static char[] MULTIPART_CHARS =
                "-_1234567890abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"
                        .toCharArray();

        private final static String DEFAULT_SUBTYPE = "form-data";

        private ContentType contentType;
        private HttpMultipartMode mode = HttpMultipartMode.STRICT;
        private String boundary = null;
        private Charset charset = null;
        private List bodyParts = null;

        public static MultipartEntityBuilder create() {
            return new MultipartEntityBuilder();
        }

        MultipartEntityBuilder() {
        }

        public MultipartEntityBuilder setMode(final HttpMultipartMode mode) {
            this.mode = mode;
            return this;
        }

        public MultipartEntityBuilder setLaxMode() {
            this.mode = HttpMultipartMode.BROWSER_COMPATIBLE;
            return this;
        }

        public MultipartEntityBuilder setStrictMode() {
            this.mode = HttpMultipartMode.STRICT;
            return this;
        }

        public MultipartEntityBuilder setBoundary(final String boundary) {
            this.boundary = boundary;
            return this;
        }

        /**
         * @since 4.4
         */
        public MultipartEntityBuilder setMimeSubtype(final String subType) {
            Args.notBlank(subType, "MIME subtype");
            this.contentType = ContentType.create("multipart/" + subType);
            return this;
        }

        /**
         * @since 4.4
         *
         * @deprecated (4.5) Use {@link #setContentType(org.apache.http.entity.ContentType)}.
         */
        @Deprecated
        public MultipartEntityBuilder seContentType(final ContentType contentType) {
            return setContentType(contentType);
        }

        /**
         * @since 4.5
         */
        public MultipartEntityBuilder setContentType(final ContentType contentType) {
            Args.notNull(contentType, "Content type");
            this.contentType = contentType;
            return this;
        }

        public MultipartEntityBuilder setCharset(final Charset charset) {
            this.charset = charset;
            return this;
        }

        /**
         * @since 4.4
         */

    }


