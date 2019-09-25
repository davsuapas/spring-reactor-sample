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

package carisa_deployer

import (
	"carisa/core/boots"
	"carisa/deployer/global"
	"os"
)

func main() {
	loadConfig(os.Args)
}

func loadConfig(params []string) {
	serviceName, local, env := boots.MainParams(params)
	global.LoadConfig(serviceName, local, env)
}
