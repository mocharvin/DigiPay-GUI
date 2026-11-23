// Abstract class sebagai parent class (superclass)
abstract class MetodePembayaran {
    private String namaMetode;

    public MetodePembayaran(String namaMetode) {
        this.namaMetode = namaMetode;
    }

    public String getNamaMetode() {
        return namaMetode;
    }

    // Ubah dari 'void' menjadi 'String'
    public abstract String prosesPembayaran(double jumlah);
}