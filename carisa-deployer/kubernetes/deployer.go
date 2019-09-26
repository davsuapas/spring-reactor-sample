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

// Deploy kubernetes environment
package kubernetes

import (
	"k8s.io/client-go/kubernetes"
	"k8s.io/client-go/kubernetes/typed/core/v1"
	"k8s.io/client-go/tools/clientcmd"
	"log"
)

type Deployer struct {
	configPath string
}

func NewDeployer(configPath string) *Deployer {
	return &Deployer{configPath}
}

// Create creates kubernetes namespace
func CreateNamespace() {

}

func (deployer *Deployer) client() v1.CoreV1Interface {
	config, err := clientcmd.BuildConfigFromFlags("", deployer.configPath)
	clientSet, err := kubernetes.NewForConfig(config)
	if err != nil {
		log.Fatal(err)
	}
	return clientSet.CoreV1()
}
