# Kern
### Keyboard-First Android Launcher

## Overview

**Kern** adalah launcher Android minimalis yang mengadopsi filosofi visual Olauncher namun mengusung konsep *workflow launcher* seperti Raycast dan Alfred. Berbeda dengan launcher konvensional yang berpusat pada ikon aplikasi, Kern menjadikan **command bar** sebagai pusat seluruh interaksi pengguna.

Home screen hanya menampilkan jam, tanggal, dan sebuah command bar. Dari command bar tersebut pengguna dapat mencari aplikasi, menjalankan command bawaan, membuka aplikasi eksternal melalui Android Intent, serta mengakses berbagai fungsi dengan cara mengetik, bukan menavigasi menu.

Filosofi utama Kern adalah **keyboard-first interaction**, yaitu meminimalkan jumlah sentuhan layar dan memaksimalkan kecepatan akses melalui teks.

---

# Vision

Membuat launcher yang terasa seperti shell pada sistem operasi desktop, namun dirancang khusus untuk Android.

Alih-alih membuka App Drawer, mencari ikon, lalu menekan aplikasi, pengguna cukup mengetik:

```text
spotify
```

atau

```text
maps coffee
```

atau

```text
timer 25m
```

Seluruh aksi dijalankan dari satu tempat.

---

# Design Principles

- Minimalistic UI
- Keyboard First
- Fast Search
- Instant Command Execution
- Zero Visual Clutter
- Personal Workflow
- Modular Architecture
- Extensible Command System

---

# Early Prototype Development Plan

---

# Phase 0 — Project Setup

## Objective

Membangun fondasi launcher Android yang dapat dijadikan Home Launcher.

## Features

- Kotlin + Jetpack Compose
- Launcher Intent
- Default Home Launcher
- Minimal Home Screen
- Clock
- Date
- Command Bar

## Deliverable

Saat launcher dibuka pengguna hanya melihat:

```text
09:41

Tuesday

> _
```

Belum terdapat pencarian maupun command.

---

# Phase 1 — App Search

## Objective

Membuat launcher dapat menggantikan launcher biasa.

## Features

- Scan installed apps
- App Index
- Real-time Search
- Launch App
- Search Result List

## Flow

```text
> spo

Spotify
Spotify Lite
```

Tekan Enter

↓

Spotify terbuka.

## Deliverable

Launcher telah mampu membuka seluruh aplikasi yang terpasang melalui command bar.

---

# Phase 2 — Search Ranking

## Objective

Meningkatkan kualitas pencarian agar terasa lebih pintar.

## Features

- Prefix Matching
- Usage Frequency
- Recent Apps
- Dynamic Ranking

## Example

Awalnya

```text
ca
```

```text
Calculator
Camera
Calendar
Canva
```

Setelah Canva sering digunakan

```text
Canva
Calculator
Camera
Calendar
```

## Deliverable

Launcher mulai menyesuaikan hasil pencarian berdasarkan kebiasaan pengguna.

---

# Phase 3 — Command System

## Objective

Mengubah launcher dari sekadar pencarian aplikasi menjadi command launcher.

## Features

- Input Tokenizer
- Command Parser
- Command Object
- Command Executor

## Example

Input

```text
maps jakarta
```

Parser

```text
Command
type = MAPS
argument = jakarta
```

Executor

↓

Open Google Maps

## Deliverable

Launcher mampu membedakan antara nama aplikasi dan command.

---

# Phase 4 — Built-in Commands

## Objective

Menambahkan command bawaan.

## Features

- Google Search
- Google Maps
- YouTube Search
- Calculator
- Timer
- Contact Calling

## Examples

```text
maps bandung
```

↓

Google Maps

---

```text
yt raycast
```

↓

YouTube Search

---

```text
calc 15*12
```

↓

180

---

```text
timer 20m
```

↓

Start Timer

---

```text
call mom
```

↓

Open Contact

## Deliverable

Launcher mampu menjalankan aksi tanpa harus membuka aplikasi terlebih dahulu.

---

# Phase 5 — UI Refinement

## Objective

Menyempurnakan pengalaman pengguna.

## Features

- Smooth Animation
- Keyboard Auto Focus
- Better Search Result
- Dark Theme
- Improved Layout
- Responsive Interaction

## Deliverable

Launcher terasa cepat, ringan, dan nyaman digunakan setiap hari.

---

# Phase 6 — History & Personalization

## Objective

Membuat launcher mulai belajar dari perilaku pengguna.

## Features

- Command History
- Recent Commands
- Recent Apps
- Usage Statistics

## Example

History

```text
yt raycast

maps coffee

timer 25m
```

User cukup menekan tombol ↑ untuk menjalankan command sebelumnya.

## Deliverable

Launcher mulai terasa personal sesuai pola penggunaan.

---

# Phase 7 — Alias & Settings

## Objective

Memberikan personalisasi kepada pengguna.

## Features

Alias

```text
yt
```

↓

YouTube

---

```text
gh
```

↓

GitHub

---

```text
g
```

↓

Google Search

Settings

- Theme
- Clock Format
- History
- Alias Management
- Search Preference

## Deliverable

Launcher menjadi lebih fleksibel dan dapat disesuaikan dengan preferensi masing-masing pengguna.

---

# Final Prototype Context

Setelah seluruh fase selesai, Kern menjadi sebuah launcher Android berbasis command bar yang mampu menggantikan launcher konvensional untuk aktivitas sehari-hari.

Ketika launcher dibuka, pengguna langsung disajikan antarmuka yang bersih.

```text
09:41

Tuesday

> _
```

Cursor langsung aktif sehingga pengguna dapat segera mengetik.

---

## Opening Applications

```text
spo
```

↓

```
Spotify
Spotify Lite
```

Enter

↓

Spotify terbuka.

---

## Searching Maps

```text
maps coffee shop
```

↓

Google Maps membuka pencarian *coffee shop*.

---

## YouTube Search

```text
yt lofi jazz
```

↓

YouTube membuka hasil pencarian.

---

## Calculator

```text
calc 250*18
```

↓

```
4500
```

---

## Timer

```text
timer 30m
```

↓

Timer Android dimulai.

---

## Contact

```text
call mom
```

↓

Halaman panggilan kontak terbuka.

---

## Dynamic Search Ranking

Semakin sering suatu aplikasi digunakan, semakin tinggi posisinya.

Contoh:

```text
ch
```

Pengguna A

```
Chrome
ChatGPT
Chess
```

Pengguna B

```
ChatGPT
Chrome
Chess
```

Ranking akan berbeda berdasarkan kebiasaan masing-masing.

---

## History

Launcher menyimpan command sebelumnya.

```
yt raycast

maps bandung

timer 20m
```

Command dapat dijalankan kembali tanpa mengetik ulang.

---

## Alias

Pengguna dapat membuat alias sendiri.

```
yt
```

↓

YouTube

```
gh
```

↓

GitHub

```
maps
```

↓

Google Maps

---

# Typical Workflow

Contoh penggunaan sehari-hari.

```
Launcher

↓

spo

↓

Spotify

↓

Launcher

↓

timer 25m

↓

Timer

↓

Launcher

↓

maps campus

↓

Google Maps

↓

Launcher

↓

calc 18*24

↓

432

↓

Launcher

↓

yt raycast

↓

YouTube
```

Seluruh interaksi dilakukan hanya melalui command bar.

---

# Internal Architecture

```
User Input
        │
        ▼
 Command Bar
        │
        ▼
 Input Parser
        │
 ┌──────┴──────┐
 │             │
 ▼             ▼
App Search   Command Parser
 │             │
 ▼             ▼
Ranking     Command Object
 │             │
 └──────┬──────┘
        ▼
 Search Result
        │
        ▼
Command Executor
        │
        ▼
 Android Intent
        │
        ▼
 Application / Maps / YouTube / Timer / Contact
```

---

# Project Structure

```
app/

├── ui/
│   ├── home/
│   ├── search/
│   ├── settings/
│   └── components/
│
├── command/
│   ├── parser/
│   ├── executor/
│   ├── builtin/
│   └── model/
│
├── search/
│   ├── engine/
│   ├── ranking/
│   └── index/
│
├── data/
│   ├── repository/
│   ├── room/
│   └── datastore/
│
├── service/
│   ├── launcher/
│   └── intent/
│
├── model/
│
└── util/
```

---

# Technology Stack

| Category | Technology |
|----------|------------|
| Language | Kotlin |
| UI | Jetpack Compose |
| Architecture | MVVM |
| Dependency Injection | Hilt |
| Database | Room |
| Preferences | DataStore |
| Async | Kotlin Coroutines + Flow |
| Navigation | Navigation Compose |
| Search | Custom Search Engine |
| Android APIs | PackageManager, Intent, Contacts, Alarm, Search |
| Build Tool | Gradle |

---

# Out of Scope (Future Phases)

Fitur berikut **belum termasuk** dalam prototype dan direncanakan untuk pengembangan selanjutnya.

- AI Assistant
- Clipboard Summarization
- Translation
- Plugin System
- Workflow Automation
- Git Integration
- File Indexing
- Cloud Sync
- Cross-device Synchronization
- Natural Language Command Understanding
- Third-party Extensions

Prototype ini difokuskan untuk memvalidasi konsep **keyboard-first launcher** dan membangun fondasi arsitektur yang kuat sebelum berkembang menjadi workflow launcher Android yang lebih lengkap.
