# Multi Fenêtre+

Appli Android qui lance **2 applis ou plus en même temps**, chacune dans sa fenêtre, avec la disposition de ton choix.

## Fonctions

- Choix de **2 à N applis** (l'ordre de sélection = l'ordre des fenêtres)
- 4 dispositions : **Grille**, **Colonnes**, **Lignes**, **1 grande + petites**
- Réglage de l'**espace entre les fenêtres**
- **Raccourcis** : enregistre un combo d'applis + disposition et relance-le en un clic
- Recherche dans la liste des applis

## Compiler l'APK

1. Installe **Android Studio** sur un PC.
2. `File > Open` et choisis le dossier `MultiFenetre`.
3. Attends la synchronisation Gradle.
4. Branche ton téléphone (débogage USB activé) et clique sur **Run**, ou fais `Build > Build APK(s)`.

## À activer sur le téléphone (important)

Android ne laisse pas une appli normale placer librement des fenêtres. Il faut activer les fenêtres libres :

1. Réglages > À propos du téléphone > Informations sur le logiciel > appuie 7 fois sur **Numéro de build** (active les options pour développeurs).
2. Réglages > Options pour développeurs, active :
   - **Activer les fenêtres freeform** (ou « Freeform windows »)
   - **Forcer le redimensionnement des activités** (« Force activities to be resizable »)
3. Redémarre le téléphone.

Sur Samsung (One UI), si ça se comporte mal, active aussi **Samsung DeX** ou le mode « Vue en fenêtre » depuis les réglages Fonctions avancées.

## Limites

- Certaines applis (banques, certains jeux) refusent d'être redimensionnées ou mises en fenêtre : elles s'ouvriront en plein écran.
- Le placement exact dépend de la version d'Android et de One UI : à tester sur ton appareil, les dispositions sont faciles à ajuster dans `WindowLayouts.kt`.
