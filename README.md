# logflow (Increment 2)

## Çalıştırma
    mvn verify
    java -cp target/classes logflow.Main data/access-small.log

Her geçerli satır yapılandırılmış kayıt olarak yazdırılır; hatalı satırlar sayılır ve sonda toplam yazdırılır.

## Test ve kapsama
`mvn verify` -> `target/site/jacoco/index.html`
Satır kapsama (line coverage): **%XX** (JaCoCo raporundaki "Lines" değerini buraya yaz)

## Bilinen sınırlama
Hatalı satırlar şimdilik yalnızca atlanır ve sayılır; doğru ele alma 5. haftada gelecek.
