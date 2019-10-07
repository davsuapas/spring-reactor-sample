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

// Log configuration. Use logrus base
package boots

import (
	log "github.com/sirupsen/logrus"
	"os"
)

type LogConfiguration struct {
	Level  string `yaml:"level"`
	Format string `yaml:"format,omitempty"`
	Output string `yaml:"output,omitempty"`
}

// Load log configuration
func LoadLog(config *LogConfiguration) {

	const INFO = "info"
	const WARNING = "warning"
	const ERROR = "error"
	const DEBUG = "debug"
	const PANIC = "panic"
	const JSON = "json"
	const STDOUT = "stdout"

	if !(config.Level == INFO ||
		config.Level == WARNING ||
		config.Level == ERROR ||
		config.Level == DEBUG ||
		config.Level == PANIC) {
		panic("log level must be configured to (info | warning | error | debug | panic)")
	}

	if !(config.Format == "standard" || config.Format == JSON || config.Format == "") {
		panic("log format must be configured to (standard | json)")
	}

	if !(config.Output == "stderr" || config.Output == STDOUT || config.Output == "") {
		panic("log output must be configured to (stderr | stdout)")
	}

	switch config.Level {
	case INFO:
		log.SetLevel(log.InfoLevel)
	case WARNING:
		log.SetLevel(log.WarnLevel)
	case ERROR:
		log.SetLevel(log.ErrorLevel)
	case DEBUG:
		log.SetLevel(log.DebugLevel)
	case PANIC:
		log.SetLevel(log.PanicLevel)
	}

	if config.Format == JSON {
		log.SetFormatter(&log.JSONFormatter{})
	}

	if config.Output == STDOUT {
		log.SetOutput(os.Stdout)
	}
}
