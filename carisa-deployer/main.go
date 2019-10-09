//+build !test

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

package main

import (
	"carisa/core/web"
	"carisa/deployer/configuration"
	"carisa/deployer/global"
	"os"
)

func main() {
	config := global.LoadConfig(os.Args)
	w := configuration.NewWebServer(configuration.NewService(config))
	web.
		ConfigureServer().
		Routes(w.Routes).
		Start(config.Server.Port)
}
