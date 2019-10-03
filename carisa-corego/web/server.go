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

// Server configuration
package web

import (
	"github.com/labstack/echo/v4"
	"github.com/labstack/echo/v4/middleware"
	"strconv"
)

type Web struct {
	e *echo.Echo
}

// Configure initialize web server
func ConfigureServer() *Web {
	e := echo.New()
	e.Use(middleware.Logger())
	e.Use(middleware.Recover())
	web := &Web{e}
	return web
}

// Routes is route template.
func (web *Web) Routes(r func(*echo.Echo)) *Web {
	r(web.e)
	return web
}

// Start server
func (web *Web) Start(port int) {
	web.e.Logger.Fatal(web.e.Start(":" + strconv.Itoa(port)))
}
