# ✈️ CarthagoBooking

## 🧭 Présentation

**CarthagoBooking** est une application de réservation de voyages tout-en-un développée dans le cadre du projet **PIDEV 3A** à l’École d’ingénierie **Esprit**. Elle permet aux utilisateurs de planifier, réserver et gérer facilement leurs hébergements, vols, offres spéciales et circuits touristiques guidés.

Cette version est développée en **Java avec JavaFX**, offrant une interface utilisateur moderne, fluide et interactive, intégrant des fonctionnalités avancées comme la reconnaissance faciale, les paiements Stripe, les QR codes et bien plus encore.

## ✨ Fonctionnalités

- 👤 **Gestion des utilisateurs** : inscription, connexion sécurisée, profils, rôles (Client, Guide, Admin), OAuth Google, récupération de mot de passe.
- 🏨 **Hébergements** : recherche, filtrage, réservation, statistiques.
- ✈️ **Vols** : moteur de recherche multi-critères (compagnie, durée, prix, météo).
- 🎁 **Offres spéciales** : packages personnalisés (vol + hôtel + activité).
- 🌍 **Circuits & Guides** : profils de guides, réservation d’activités, itinéraires.
- 🏆 **Programme de fidélité** : points, niveaux (Bronze à Diamond), QR codes.
- 💳 **Paiement** : intégration Stripe.
- 📊 **Dashboard admin** : gestion utilisateurs, offres, guides, statistiques.
- 📄 **PDF** : génération de factures, reçus, itinéraires.
- ⭐ **Avis clients** : commentaires et notes.
- 💬 **Support en ligne** : messagerie instantanée.
- 🧠 **Reconnaissance faciale** : service Python intégré.
- 🗓️ **Calendrier interactif** : gestion des réservations de tours.
- 🧭 **Tours personnalisés** : ajout d’activités, géolocalisation.

## 🛠️ Stack Technique

**Technologies** :
- Java 21
- JavaFX (UI)
- MySQL / MariaDB
- JDBC / Hibernate (ORM)
- iText ou PDFBox (PDF)
- ZXing (QR Code)
- Python + OpenCV (Reconnaissance faciale)
- Stripe API
- OAuth Google
- JFoenix / ControlsFX (UI avancée)

## 🚀 Installation & Lancement

### 1. Cloner le projet

```bash
git clone <url-du-repo>
cd CarthagoBooking
```

### 2. Configurer la base de données

Modifier le fichier `config.properties` ou `application.conf` :

```properties
db.url=jdbc:mysql://localhost:3306/carthagobooking
db.user=root
db.password=
stripe.secret_key=VOTRE_CLE_PRIVEE
stripe.public_key=VOTRE_CLE_PUBLIQUE
google.client_id=VOTRE_CLIENT_ID
google.client_secret=VOTRE_CLIENT_SECRET
```

### 3. Construire le projet

Avec Maven :

```bash
mvn clean install
```

Ou Gradle :

```bash
./gradlew build
```

### 4. Lancer l’application

Depuis votre IDE (IntelliJ / Eclipse) ou en ligne de commande :

```bash
java -jar target/carthagobooking.jar
```

### 5. Lancer le service de reconnaissance faciale (Python)

```bash
cd face_recognition_service
python3 app.py
```

Disponible sur : [http://localhost:5001](http://localhost:5001)

### 6. (Optionnel) Charger les données de démonstration

```bash
mvn exec:java -Dexec.mainClass="com.carthago.utils.DemoDataLoader"
```

## 🧪 Tests

```bash
mvn test
```

## 🌐 Accès

- Application JavaFX : via exécutable ou IDE
- Reconnaissance faciale : [http://localhost:5001](http://localhost:5001)
- Connexion Google : via WebView JavaFX

## 🎓 Remerciements

Projet réalisé dans le cadre du cours **PIDEV 3A** à l’École d’ingénierie **Esprit**.

## 📄 Licence

Projet éducatif. Merci de respecter les licences des bibliothèques utilisées.
