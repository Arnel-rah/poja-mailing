## Test de la fonctionnalité

### En local
1. Démarrer l'application (base de données, stockage S3/LocalStack si utilisé et workers Poja).
2. Envoyer un `POST /uploaded-files` avec un fichier PNG et un email.
3. Vérifier que la réponse est `202 Accepted`.
4. Appeler `GET /uploaded-files` pour confirmer que le statut passe de `PENDING` à `PROCESSED`.
5. Vérifier la réception de l'email contenant le lien vers l'image en noir et blanc.

### En preprod
1. Activer **File Storage** et configurer **Queues NB = 2** dans Poja.
2. Déployer l'application.
3. Répéter le test avec `POST /uploaded-files` et vérifier le changement de statut ainsi que la réception de l'email.