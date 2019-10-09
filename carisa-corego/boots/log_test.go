/*
 *  Copyright 2019-2022 the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package boots

import (
	log "github.com/sirupsen/logrus"
	"github.com/stretchr/testify/assert"
	"testing"
)

func TestLoadLogWithLevelWrong(t *testing.T) {
	assert.Panics(t, func() { LoadLog(&LogConfiguration{Level: "other"}, "sn") }, "should do panic")
}

func TestLoadLogWithFormatWrong(t *testing.T) {
	assert.Panics(t, func() { LoadLog(&LogConfiguration{Format: "other"}, "sn") }, "should do panic")
}

func TestLoadLogWithOutputWrong(t *testing.T) {
	assert.Panics(t, func() { LoadLog(&LogConfiguration{Output: "other"}, "sn") }, "should do panic")
}

func TestLoadLogWithLogFormat(t *testing.T) {
	LoadLog(&LogConfiguration{Level: "info", Format: "json", Output: "stdout"}, "sn")

	assert.IsType(t, &log.JSONFormatter{}, log.StandardLogger().Formatter, "should return json formatter")
}

func TestLoadLogWithStandardFormat(t *testing.T) {
	LoadLog(&LogConfiguration{Level: "info", Format: "standard"}, "sn")

	assert.IsType(t, &log.TextFormatter{}, log.StandardLogger().Formatter, "should return standard formatter")
}

func TestLoadLogWithInfoLevel(t *testing.T) {
	LoadLog(&LogConfiguration{Level: "info"}, "sn")

	assert.Equal(t, log.InfoLevel, log.GetLevel())
}

func TestLoadLogWithWarningLevel(t *testing.T) {
	LoadLog(&LogConfiguration{Level: "warning"}, "sn")

	assert.Equal(t, log.WarnLevel, log.GetLevel())
}

func TestLoadLogWithErrorLevel(t *testing.T) {
	LoadLog(&LogConfiguration{Level: "error"}, "sn")

	assert.Equal(t, log.ErrorLevel, log.GetLevel())
}

func TestLoadLogWithDebugLevel(t *testing.T) {
	LoadLog(&LogConfiguration{Level: "debug"}, "sn")

	assert.Equal(t, log.DebugLevel, log.GetLevel())
}

func TestLoadLogWithPanicLevel(t *testing.T) {
	LoadLog(&LogConfiguration{Level: "panic"}, "sn")

	assert.Equal(t, log.PanicLevel, log.GetLevel())
}

func TestLogTags(t *testing.T) {
	log := LoadLog(&LogConfiguration{Level: "info"}, "serviceName")
	log = log.Module("moduleName").Function("functionName")

	assert.Equal(t, "serviceName", log.Data["service"], "should return Module name")
	assert.Equal(t, "moduleName", log.Data["module"], "should return Module name")
	assert.Equal(t, "functionName", log.Data["function"], "should return Function name")
}
