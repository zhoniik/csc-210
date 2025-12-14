import java.util.ArrayList;

public class DNAMatcher {
    public static ArrayList<String> DNAToCodons(String dna) {
        ArrayList<String> codons = new ArrayList<String>();
        if (dna == null) return codons;
        dna = dna.toUpperCase(); 
        int i = 0;
        while (i + 2 < dna.length()) {
            String codon = dna.substring(i, i + 3);
            codons.add(codon);
            i = i + 3;
        }
        return codons;
    }
    public static String CodonToAminoAcid(String codon) {
        if (codon == null || codon.length() != 3) return "?";
        codon = codon.toUpperCase();

        if (codon.equals("TTT") || codon.equals("TTC")) return "F";
        if (codon.equals("TTA") || codon.equals("TTG") ||
            codon.equals("CTT") || codon.equals("CTC") ||
            codon.equals("CTA") || codon.equals("CTG")) return "L";
        if (codon.equals("ATT") || codon.equals("ATC") || codon.equals("ATA")) return "I";
        if (codon.equals("ATG")) return "M";
        if (codon.equals("GTT") || codon.equals("GTC") || codon.equals("GTA") || codon.equals("GTG")) return "V";
        if (codon.equals("TCT") || codon.equals("TCC") || codon.equals("TCA") || codon.equals("TCG")
                || codon.equals("AGT") || codon.equals("AGC")) return "S";
        if (codon.equals("CCT") || codon.equals("CCC") || codon.equals("CCA") || codon.equals("CCG")) return "P";
        if (codon.equals("ACT") || codon.equals("ACC") || codon.equals("ACA") || codon.equals("ACG")) return "T";
        if (codon.equals("GCT") || codon.equals("GCC") || codon.equals("GCA") || codon.equals("GCG")) return "A";
        if (codon.equals("TAT") || codon.equals("TAC")) return "Y";
        if (codon.equals("TAA") || codon.equals("TAG") || codon.equals("TGA")) return "Stop";
        if (codon.equals("CAT") || codon.equals("CAC")) return "H";
        if (codon.equals("CAA") || codon.equals("CAG")) return "Q";
        if (codon.equals("AAT") || codon.equals("AAC")) return "N";
        if (codon.equals("AAA") || codon.equals("AAG")) return "K";
        if (codon.equals("GAT") || codon.equals("GAC")) return "D";
        if (codon.equals("GAA") || codon.equals("GAG")) return "E";
        if (codon.equals("TGT") || codon.equals("TGC")) return "C";
        if (codon.equals("TGG")) return "W";
        if (codon.equals("CGT") || codon.equals("CGC") || codon.equals("CGA") || codon.equals("CGG")
                || codon.equals("AGA") || codon.equals("AGG")) return "R";
        if (codon.equals("GGT")) return "G";
        if (codon.equals("GGC") || codon.equals("GGA") || codon.equals("GGG")) return "S";

        return "?";
    }
    public static ArrayList<String> dna_to_amino_acid(String dna) {
        ArrayList<String> aas = new ArrayList<String>();
        ArrayList<String> codons = DNAToCodons(dna);
        for (int i = 0; i < codons.size(); i++) {
            String aa = CodonToAminoAcid(codons.get(i));
            if (aa.equals("Stop")) {
                break;
            }
            if (!aa.equals("?")) {
                aas.add(aa);
            }
        }
        return aas;
    }
    public static boolean is_match(ArrayList<String> a, ArrayList<String> b) {
        if (a == null || b == null) return false;
        if (a.size() != b.size()) return false;
        for (int i = 0; i < a.size(); i++) {
            if (!a.get(i).equals(b.get(i))) {
                return false;
            }
        }
        return true;
    }
    public static void main(String[] args) {
        String DNA1 = "CTGATATTGTATCCGGCCGAA";
        String DNA2 = "CTAGCCGGTGGTTATTAATAGTAAACTATTCCA";
        String DNA3 = "TTAATCCTCTACCCCGCAGAG";

        ArrayList<String> aa1 = dna_to_amino_acid(DNA1);
        ArrayList<String> aa2 = dna_to_amino_acid(DNA2);
        ArrayList<String> aa3 = dna_to_amino_acid(DNA3);

        System.out.println("AA1: " + aa1);
        System.out.println("AA2: " + aa2);
        System.out.println("AA3: " + aa3);

        System.out.println("DNA1 vs DNA2: " + (is_match(aa1, aa2) ? "IDENTICAL" : "different"));
        System.out.println("DNA1 vs DNA3: " + (is_match(aa1, aa3) ? "IDENTICAL" : "different"));
        System.out.println("DNA2 vs DNA3: " + (is_match(aa2, aa3) ? "IDENTICAL" : "different"));
    }
}