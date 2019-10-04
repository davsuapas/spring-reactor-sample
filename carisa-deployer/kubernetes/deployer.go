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
	apiv1 "k8s.io/api/core/v1"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	"k8s.io/client-go/kubernetes"
	corev1 "k8s.io/client-go/kubernetes/typed/core/v1"
	"k8s.io/client-go/tools/clientcmd"
	"log"
)

type (
	deployerCreation struct {
		Namespace string
	}

	Deployer struct {
		apiCore corev1.CoreV1Interface
	}
)

func NewDeployer(configPath string) (*Deployer, error) {
	config, err := clientcmd.BuildConfigFromFlags("", configPath)

	clientSet, err := kubernetes.NewForConfig(config)
	if err != nil {
		log.Fatal(err)
	}

	return &Deployer{clientSet.CoreV1()}, err
}

// Create makes kubernetes namespace
func (deployer *Deployer) CreateNamespace(creation *deployerCreation) error {
	_, err := deployer.apiCore.Namespaces().Create(
		&apiv1.Namespace{ObjectMeta: metav1.ObjectMeta{Name: creation.Namespace}})

	if err != nil {
		log.Println("Creating Namespace in kubernetes", err)
	}

	return err
}
