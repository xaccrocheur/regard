![Lisp](https://upload.wikimedia.org/wikipedia/commons/9/99/Lisp-glossy-120.jpg)

Motivation / Besoins
-----------
Je suis tombé sur [cet article](http://thechangelog.com/frak-takes-an-entirely-different-approach-to-generating-regular-expressions/?utm_source=feedburner&utm_medium=feed&utm_campaign=Feed%3A+thechangelog+%28The+Changelog%29) du changelog qui m'a rappellé que

- J'aimerais bien construire des expressions régulières en commençant par la fin, C à D passer des chaînes de caractères à un générateur qui me sortirait l'expression ad-hoc (1) pas toi ?
- Ça fait un moment que [Clojure](http://clojure.org/) me fait de l'oeil, c'est le moment de plonger, non ?

(1) Normalement pour ça j'utilise le [regexp-builder d'Emacs](http://www.masteringemacs.org/articles/2011/04/12/re-builder-interactive-regexp-builder/), qui te permet de voir directement ce qui matche dans le buffer courant. Donc dans l'autre sens : D'abord le code, ensuite les données. Là, on va prendre l'autre route, et tenter d'apprendre quelque chose en chemin :)

![Clojure](http://clojure.org/file/view/clojure-icon.gif)

Clojure
-----------
Selon la page wikipedia, "Rich Hickey a développé Clojure parce qu'il voulait un [Lisp](http://fr.wikipedia.org/wiki/Lisp) moderne pour la [programmation fonctionnelle](http://fr.wikipedia.org/wiki/Programmation_fonctionnelle), en symbiose avec la [plateforme Java](http://fr.wikipedia.org/wiki/Java_(technique)), et expressément orienté vers la [programmation concurrente](http://fr.wikipedia.org/wiki/Programmation_concurrente)" et tout ça fait ma foi de la lecture pour le train.
Clojure permet d'attaquer la VM Java en Lisp ([repl online](http://tryclj.com/)) ce qui est formidable puisque Java est omnipuissant et que Lisp rend intelligent :)
Clojure compile to JS ([repl online](http://himera.herokuapp.com/index.html)) grâce à [clojurescript](http://clojure.org/clojurescript).

- [Documentation de référence](http://clojure.org/documentation)
- [REPL en ligne](http://tryclj.com/)

![Leiningen](http://leiningen.org/img/leiningen.jpg)

Leiningen
-----------

*"Leiningen!" he shouted. "You're insane! They're not creatures you can fight—they're an elemental—an 'act of God!' Ten miles long, two miles wide—ants, nothing but ants! And every single one of them a fiend from hell..."*

*"Leiningen Versus the Ants" by Carl Stephenson*

[Leiningen](http://leiningen.org/) semble à peu près incontournable pour utiliser clojure "without setting your hair on fire" et de fait, [la doc](http://leiningen.org/#docs) est un point d'entrée remarquable vers le langage lui-même. Dans [le tutorial](https://github.com/technomancy/leiningen/blob/stable/doc/TUTORIAL.md) (2) Leinigen se présente comme suit "If you come from the Java world, Leiningen could be thought of as "Maven meets Ant without the pain". For Ruby and Python folks, Leiningen combines RubyGems/Bundler/Rake and pip/Fabric in a single tool". Autant dire que c'est pas rien.

Leiningen va donc s'occuper

- De créer l'arborescence du projet
- De permettre la mise en place et l'exécution d'un REPL (voir + loin) pour évaluer du code dans l'evironnement même du projet (pas juste dans un shell interactif comme celui de Ruby, par exemple)
- De télécharger et gérer primitivement les versions des diverses dépendances (bibliothèques / libs) à l'exécution
- De mettre en place et faire tourner des tests
- D'envoyer un SMS à ton conjoint comme quoi tu auras un peu de retard
- De compiler vers des cibles diverses, contre les libs précitées (statique) ou non (dynamique)

(2) Le tuto de Leininger est disponible à tout moment en tapant `lein help tutorial`

Bon, on code ?
-----------
Première chose, installer Leiningen, qui doit se trouver dans les paquets de ta distro.

`sudo apt-get install leiningen`

chez moi, 68 paquets. Oui, tout de même.

Maintenant on va créer un projet en utilisant le template "app" (par opposition à celui qu'on utiliserait pour créer une librairie, par exemple) on va l'appeler **regard** parce que c'est joli.

```sh
$ lein new regard
Created new project in: (NDLR : obfustated)regard
Look over project.clj and start coding in regard/core.clj
```

(Oui, le tuto dit de passer le template en paramètre, je sais, fais ce que je te dis et si tu as des questions t'es gentil(le) tu lèves la main)

À ce stade on a cette arborescence :

```sh
$ cd regard
$ find .
.
./src
./src/regard
./src/regard/core.clj
./test
./test/regard
./test/regard/test
./test/regard/test/core.clj
./README
./project.clj
./.gitignore
```

Et c'est dans cet environnement qu'on va exécuter Clojure, dont on va tout de suite spécifier qu'on en souhaite une version récente, en ouvrant le fichier `./project.clj` qui doit ressembler à ceci :

```lisp
(defproject regard "1.0.0-SNAPSHOT"
  :description "FIXME: write description"
  :dependencies [[org.clojure/clojure "1.3.0"]])
```

Et qu'on va éditer pour qu'il ressemble à cela :

```lisp
(defproject regard "1.0.0-SNAPSHOT"
  :description "Spits out regexps, given keywords"
  :dependencies [[org.clojure/clojure "1.5.1"]
                [frak "0.1.3"]]
  :main regard.core
  :aot [regard.core]
  :uberjar-name "regard.jar")
```

Donc dans l'ordre

- defproject - note la forme (1 2 3 n) où 1 est une fonction et tout le reste des arguments passés à cette dernière - voir plus loin pour la doc de cette fonction
- La description du projet
- Les dépendances dont le projet a besoin (Leiningen s'occupera de les télécharger, voir + loin) et [Clojure](https://github.com/clojure/clojure) en est une (note le bump de version) ainsi que [frak](https://github.com/noprompt/frak)
- Le nom de la fonction d'entrée du programme, pour éviter de la passer à chaque fois en paramètre : `Providing a -m argument will tell Leiningen to look for the -main function in another namespace. Setting a default :main in project.clj lets you omit -m`.
- Le paramètre de compilation "Ahead Of Time" nécessaire plus tard à la compilation du binaire standalone (statique)
- Le nom de notre exécutable final


Maintenant on va coder le programme à proprement parler, en ouvrant `core.clj` et en recopiant savamment ceci :

```lisp
(ns regard.core
  (:gen-class))

(require 'frak)

(defn -main [& args]
  "Take ARGS and pass them to frak, insult the user otherwise."
  (if args
    (println "Regexp :" (frak/pattern (vec args)))
    (println "usage: regard \"pattern1\" \"pattern2\" \"patternN\"")))
```

Dans l'ordre

- La déclaration `(:gen-class)` sous la forme *ns* correspondant au namespace spécifié plus haut dans `project.clj`
- Le require de la librairie frak, qui est déjà disponible car elle sera téléchargée au run-time - pour être compilée / linkée avec l'exécutable final - si elle est absente où pas à jour (même chose pour clojure, qu'on a juste pas besoin de requirer) - note le "quote" et faisons court : le quote permet de ne pas évaluer l'expression (un mot seul, non entouré de parenthèses, en principe c'est une variable) et de passer le *mot* **frak** littéralement.
- La déclaration de la [fonction](http://clojure-doc.org/articles/language/functions.html) qui suit les règles de Lisp (pratiques sinon stylistiques : note la bizarre indentation de la forme if ((if condition then else) où then et else sont des expressions, et non des mots-clefs) alors qu'[en emacs lisp elles sont décalées, et c'est nettement plus lisible](https://www.gnu.org/software/emacs/manual/html_node/eintr/else.html), bref) tiens ben on va lancer le [repl](https://en.wikipedia.org/wiki/Read-eval-print_loop) pour obtenir de la doc sur la fonction `defn` shall we d'accord ?

Read, Eval, Print Loop
-----------

```sh
$ lein repl
Copying 3 files to (NDLR : obfustated)regard/lib
REPL started; server listening on localhost port 4286
regard.core=>
```

Nous sommes dans l'environnement de notre programme. Pour la doc promise plus haut, on va entrer

```lisp
regard.core=> (doc defn)

clojure.core/defn
([name doc-string? attr-map? [params*] body] [name doc-string? attr-map? ([params*] body) + attr-map?])
Macro
  Same as (def name (fn [params* ] exprs*)) or (def
    name (fn ([params* ] exprs*)+)) with any doc-string or attrs added
    to the var metadata
nil
```

Qui nous dit tout ce qu'il y a à savoir sur la fonction en question (comme C-h f FUNCTION_NAME dans emacs) qui est en fait une macro.

Pendant que tu y est, entre quelques formes [Lisp](https://en.wikipedia.org/wiki/Lisp_%28programming_language%29) pour te faire la main :

```lisp
regard.core=> "Hello World!"
"Hello World!"
regard.core=> (+ 1 1)
2
regard.core=> true
true
regard.core=> false
false
regard.core=> nil
nil
regard.core=> ()
()
regard.core=> (+ 1 2 3)
6
(javax.swing.JOptionPane/showMessageDialog nil "Linuxfr est un nid à trolls") ; Huhu ;)
```

Et répète 7 fois en oscillant d'avant en arrière "En [Lisp](http://www.cs.sfu.ca/CourseCentral/310/pwfong/Lisp/1/tutorial1.html), tout sauf false / nil évalue à true".

Éxécution, allez, allez, pas d'discussion
-----------
Ctrl-c pour sortir du repl, maintenant on va exécuter notre générateur de regexp :

```sh
$ lein run
usage: app_name "pattern1" "pattern2" "patternN"
$ lein run "plip" "plop"
Regexp : #"pl(?:[oi]p)"
```
Tu viens de faire ton premier programme en Clojure, c'est pas l'heure ~~de l'apéro~~ du goûter que j'entends ? Atta, juste un dernier truc :

Compilation
-----------

```sh
$ lein uberjar
Copying 3 files to (NDLR : obfustated)/regard/lib
Compiling regard.core
Compiling regard.core
Compilation succeeded.
Created (NDLR : obfustated)/regard/regard-1.0.0-SNAPSHOT.jar
Including regard-1.0.0-SNAPSHOT.jar
Including frak-0.1.3.jar
Including optparse-1.1.1.jar
Including clojure-1.5.1.jar
Created (NDLR : obfustated)/regard/regard.jar
```

```sh
$ java -jar regard.jar "plip" "plop"
Regexp : #"pl(?:[oi]p)"
```

Que sais-je ? Pas grand' chose ;)
-----------

Nous avons maintenant un outil bien sympa, compilé, portable, pour nous aider à élaborer des expressions régulières amoureusement chantournées, et nous l'avons réalisé en Java en parlant à la JVM sur un ton qu'on n'avait pas jusqu'ici l'habitude d'employer.
Nous avons un environnement

- d'évaluation ;
- de documentation ;
- de développement ;
- de tests (on verra les tests + tard, quand j'en aurai fait en fait ;) ;
- de compilation.

Pour ce qui est du code, nous avons appris à récupérer les arguments passés en ligne de commande pour les passer en paramètres à une fonction.

### Ressources

- Le [fichier core.clj](https://github.com/xaccrocheur/regard/blob/master/src/regard/core.clj)
- Le [fichier project.clj](https://github.com/xaccrocheur/regard/blob/master/project.clj)
- Le [repo github du projet](https://github.com/xaccrocheur/regard)
- Le [source de ce journal](https://github.com/xaccrocheur/regard/blob/master/linuxfr-regard.md) (pour diffs éventuels)

Et maintenant ?
-----------

- THE [Clojure tuto](http://java.ociweb.com/mark/clojure/article.html) (bon WEnd à tous ;))
- Liste non-exhaustive de [ressources Clojure](http://dev.clojure.org/display/doc/Getting+Started) (frameworks divers (web, etc.), synthèse audio, wrappers swing, whatnot)
