# Tucil 3 - Strategi Algoritma

Tugas Kecil 3 mata kuliah Strategi Algoritma - Institut Teknologi Bandung

## Penulis

- **Muhammad Nafis Habibi** (13524018)
- **Dzaki Ahmad Al Hussainy** (13524084)

## Daftar Isi

- [Deskripsi](#deskripsi)
- [Persyaratan](#persyaratan)
- [Instalasi](#instalasi)
- [Cara Menggunakan](#cara-menggunakan)
- [Struktur Perangkat Lunak](#struktur-perangkat-lunak)
- [Algoritma](#algoritma)
- [Lisensi](#lisensi)

## Deskripsi

**Ice Sliding Puzzle** adalah permainan logika di mana pemain harus menggerakkan karakter dari titik awal menuju titik keluar di atas permukaan es yang licin. 

### Tujuan Program

Program ini dirancang untuk **memberikan solusi otomatis** terhadap permainan Ice Sliding Puzzle menggunakan berbagai algoritma pathfinding. Program menganalisis konfigurasi permainan dan mencari rute optimal dari posisi awal ke posisi target dengan membandingkan performa beberapa algoritma pencarian.

### Mekanik Permainan

- **Pin/Pemain** (visualisasi warna biru): Hanya dapat bergerak secara horizontal atau vertikal
- **Permukaan Es yang Licin**: Karakter tidak akan berhenti bergerak sampai menabrak dinding atau rintangan
- **Rintangan** (visualisasi warna putih): Halangan yang menghalangi pergerakan
- **Tujuan**: Mencapai titik keluar dengan langkah seminimal mungkin

### Algoritma Solusi

Program mengimplementasikan algoritma pencarian heuristik dan uninformed search untuk menemukan jalur optimal:
- **A\*** - Pathfinding dengan heuristik (paling optimal)
- **Greedy Best-First Search (GBFS)** - Berbasis heuristik
- **Uniform Cost Search (UCS)** - Berbasis biaya
- **Breadth-First Search (BFS)** - Pencarian menyeluruh


## Persyaratan

- **Java**: JDK 21 LTS atau lebih tinggi
- **Gradle**: Versi 8.x atau lebih tinggi
- **Sistem Operasi**: macOS, Linux, atau Windows

## Instalasi

### 1. Clone Repository
```bash
git clone <repository-url>
cd tucil3_13524018_13524084
```

### 2. Build Proyek
```bash
./gradlew build
```

### 3. Jalankan Aplikasi
```bash
./gradlew run
```

## Cara Menggunakan

### Memulai Aplikasi
1. Jalankan aplikasi dengan perintah di atas
2. Antarmuka GUI akan terbuka

### Menu Utama
- **Load Map**: Memilih file peta dari folder `data/`
- **Select Algorithm**: Memilih algoritma yang diinginkan (A*, BFS, UCS, dll)
- **Start Pathfinding**: Menjalankan algoritma
- **Visualize**: Melihat animasi proses pencarian

### Menggunakan Data Peta
Peta dapat dimuat dari folder `data/`:
- `1-map.txt` hingga `6-map.txt`

Format file peta:
```
<dimensi_grid>
<titik_awal>
<titik_tujuan>
<grid_dengan_obstacle>
```

## Struktur Perangkat Lunak

```
app/
├── src/main/
│   ├── java/tucil3_13524018_13524084/
│   │   ├── App.java                      # Entry point aplikasi
│   │   │
│   │   ├── Core/                         # Package inti permainan
│   │   │   ├── Board.java                # Representasi papan permainan
│   │   │   ├── Tile.java                 # Komponen tile di board
│   │   │   ├── TileType.java             # Enum tipe tile (kosong, obstacle, dll)
│   │   │   ├── Player.java               # Data player/pemain
│   │   │   ├── Direction.java            # Enum arah gerakan
│   │   │   └── GameStatus.java           # Status permainan
│   │   │
│   │   ├── Solver/                       # Package solver/pathfinding
│   │   │   ├── Solver.java               # Abstract base class
│   │   │   ├── AStarSolver.java          # Implementasi A* algorithm
│   │   │   ├── GBFSSolver.java           # Implementasi Greedy BFS
│   │   │   ├── UCSSolver.java            # Implementasi UCS
│   │   │   └── Node.java                 # Node struktur untuk search tree
│   │   │
│   │   ├── Controller/                   # Package controller
│   │   │   ├── MainController.java       # Main scene controller
│   │   │   ├── GameController.java       # Game scene controller
│   │   │   └── AlgorithmController.java  # Algorithm selection logic
│   │   │
│   │   ├── GUI/                          # Package GUI rendering
│   │   │   ├── BoardGUI.java             # Rendering papan
│   │   │   ├── PlayerGUI.java            # Rendering player
│   │   │   ├── TileGUI.java              # Rendering tile dengan warna
│   │   │   └── Drawable.java             # Interface untuk drawable elements
│   │   │
│   │   ├── Animation/                    # Package animasi
│   │   │   ├── AnimationStep.java        # Satu langkah animasi
│   │   │   ├── AnimationStepBundler.java # Bundler untuk multiple steps
│   │   │   ├── PlayerMoveAnimation.java  # Animasi pergerakan player
│   │   │   └── LongAnimation.java        # Animasi durasi panjang
│   │   │
│   │   ├── FileReader/                   # Package file I/O
│   │   │   └── FileIO.java               # Membaca file peta dari data/
│   │   │
│   │   └── GameEventException/           # Package exception
│   │       ├── GameEventException.java   # Base exception
│   │       └── GameOverException.java    # Exception saat game over
│   │
│   └── resources/
│       ├── css/                          # Stylesheet aplikasi
│       │   ├── App.css                   # Style utama
│       │   ├── Theme.css                 # Theme/warna
│       │   └── Lexend.css                # Font styling
│       ├── font/                         # Font file
│       │   ├── Cascadia_Code/            # Font Cascadia Code
│       │   └── Lexend/                   # Font Lexend
│       └── view/
│           └── Main.fxml                 # Layout FXML utama
│
├── src/test/
│   ├── java/tucil3_13524018_13524084/
│   │   └── AppTest.java                  # Unit test
│   └── resources/
│
├── data/                                 # Input test case
│   ├── 1-map.txt
│   ├── 2-map.txt
│   ├── 3-map.txt
│   ├── 4-map.txt
│   ├── 5-map.txt
│   └── 6-map.txt
│
└── build.gradle.kts                      # Gradle configuration

```

### Deskripsi Package Utama

| Package                | Fungsi                                                     |
|------------------------|------------------------------------------------------------|
| **Core**               | Logika inti permainan, representasi board, tile, player    |
| **Solver**             | Implementasi algoritma A*, GBFS, UCS untuk pathfinding     |
| **Controller**         | Mengontrol alur aplikasi dan interaksi user                |
| **GUI**                | Rendering visual elemen permainan (board, player, tile)    |
| **Animation**          | Animasi smooth untuk pergerakan player & visualisasi       |
| **FileReader**         | Membaca file peta dari folder `data/`                      |
| **GameEventException** | Custom exception untuk game events                         |

## Algoritma

### Algoritma yang Diimplementasikan

| Nama     | Deskripsi                             |
|----------|---------------------------------------|
| **A\***  | Informed search dengan heuristic      |
| **UCS**  | Uniform Cost Search - Weighted search |
| **GBFS** | Greedy Best-First Search              |

### Kompleksitas Waktu & Ruang

| Algoritma | Time            | Space  |
|-----------|-----------------|--------|
| **A***    | O(b^d)          | O(b^d) |
| **UCS**   | O(E + V log V)  | O(V)   |
| **GBFS**  | O(b^d)          | O(b^d) |


### Analisis

- **Performa Terbaik**: A* dengan heuristik Manhattan distance
- **Akurasi**: Semua algoritma menemukan jalur optimal
- **Trade-off**: Waktu vs memory usage

## Lisensi

Proyek ini dilisensikan di bawah [MIT License](LICENSE).

---

**Dibuat untuk**: Tugas Kecil 3 - Strategi Algoritma ITB
**Semester**: Genap 2025/2026
