# Kubernetes Configurations

Этот каталог содержит Helm чарты для развертывания сервисов в minikube.

## Структура

- `keycloak/` - Helm чарт для Keycloak
- `gateway/` - Helm чарт для Gateway Service

## Предварительные требования

1. minikube должен быть запущен
2. Helm должен быть установлен
3. PostgreSQL должен быть развернут в namespace `keycloak`

## Развертывание

### Пошаговое развертывание

#### 1. Включить Ingress в minikube

```bash
minikube addons enable ingress
```

#### 2. Развернуть Keycloak

```bash
cd keycloak
helm install keycloak . --set keycloak.createNamespace=true
```

#### 3. Развернуть gateway

```bash
cd gateway
helm install gateway . --set gateway.createNamespace=true
```

#### 4. Проверить статус

```bash
kubectl get pods -n keycloak
kubectl get pods -n gateway
```

## Доступ к сервисам

Добавьте в файл `/etc/hosts` (или `C:\Windows\System32\drivers\etc\hosts` в Windows):

```
127.0.0.1 vault.local
127.0.0.1 keycloak.local
```

### Keycloak
- URL: http://keycloak.local
- Админ: admin/admin

### Vault
- URL: http://vault.local

## Проверка статуса

```bash
kubectl get pods -n vault
kubectl get pods -n keycloak
kubectl get svc -n vault
kubectl get svc -n keycloak
```

## Удаление

```bash
helm uninstall gateway -n gateway
helm uninstall keycloak -n keycloak
```
