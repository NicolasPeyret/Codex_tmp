# Codex Project
## Codex
#####  /!\ This project is a MVP version /!\
#####  /!\ Doc is in Github Wiki /!\

### Table of Contents
1. [BDD Access Feature](#BDDAccessFeature)
2. [Barcode Scanner Feature](#BarcodeScannerFeature)
3. [Book API Feature](#BookAPIFeature)
4. [Installation](#Installation)
5. [Libraries](#Libraries)
6. [Updates](#Updates)
7. [License](#License)


Codex is an android application developed with Java language.
- Save your books into your library
- Associate a note to a book from it's page
- Scan a barcode to read book's information

### 1. BDD Access Feature <a name="BDDAccessFeature"></a>

This project uses a SQLite database made of 2 tables beeing created on project inialisation.
The relation between Book and Note is ManyToOne
Book <1---n> Note
Book table contains a foreign key column which referes to primary key of Note column
| *my_library* | type | key |
| ------ | ------- | ------ |
| **_id** | INTEGER | PRIMARY KEY AUTOINCREMENT |
| **book_title**  | TEXT
| **book_author** | TEXT
| **book_img** | TEXT
| **book_pages** | INTEGER

| *my_notes* | type | key |
| ------ | ------- | ------ |
| **_id** |INTEGER|PRIMARY KEY AUTOINCREMENT
| **note_title**  | TEXT
| **note_content**  |TEXT
| **book_id**  |INTEGER|FOREIGN KEY references Book(_id)


### 2. Barcode Scanner Feature <a name="BarcodeScannerFeature"></a>

Barcode Scanner Feature has been released thanks to scanner-code library. This feature can be used in book creation.
If you want to automatically had book's informations, select camera icon down the layout.
It will then fill all inputs.

This feature is using camera (permission required).

### 3. Book API Feature <a name="BookAPIFeature"></a>

The Book API feature exists thanks to Google free book API :
https://www.googleapis.com/books/v1/volumes
It returns book informations related to it's isbn or title.
/!\ French book are not working precisely. Only english books for the moment.
(exemple : You are searching for Lord of the Rings book 1 tome and it will return the second one) /!\

### 4. Installation <a name="Installation"></a>

Codex requires Android Studio [Android Studio](https://developer.android.com/studio) to run.

Install the sofware and download the application files.

```sh
cd [your folder]
git clone git@github.com:NicolasPeyret/Codex_tmp.git
```

Launch the root folder with android studio et let it install dependencies et build gradle.

### 5. Libraries <a name="Libraries"></a>

Codex is currently extended with the following libraries.

| Library | Documentation |
| ------ | ------ |
| code-scanner | [https://github.com/yuriy-budiyev/code-scanner] |
| picasso | [https://square.github.io/picasso/] |

### 6. Updates <a name="Updates"></a>

...


### 7. License <a name="License"></a>

MIT
