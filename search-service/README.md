# Search Service

Elasticsearch tabanlı serbest metin arama mikroservisi.

## Kullanım
- POST `/api/v1/search` ile arama yapılır.
- Arama endpointi, gerçek AVM ve mağaza verileriyle entegre çalışır. Tüm veri senkronizasyonu canlı sistemden otomatik yapılır.

## Veri Yükleme
- Tüm veri, canlı sistemden event tabanlı olarak otomatik senkronize edilir. Manuel örnek veri yüklemeye gerek yoktur. 