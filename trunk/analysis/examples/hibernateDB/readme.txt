Popis prikladu pouzitia Hibernate
---------------------------------

1. Kompilacia a spustenie

V classpath musi byt adresar lib, ktory obsahuje kniznice (medzinym aj hibernate-3.1.jar) a 
nastavnie pre logger (log4j.properties).
V pracovnom adresari odkial sa aplikacia bude spustat musia byt subory hibernate.cfg.xml a 
User.hbm.xml, ktore obsahuju nastavnie Hibernatu a mapovanie databaze.

2. Nastavenia
user.hbm.xml - mapovanie databaze (pouziva tabulku User, skontrolujte ci sedi verzia DB).
hibernate.cfg.xml - nastavnie pripojenia k Db - nastavte si cestu k databazi (hibernate.connection.url), meno a heslo
log4j.properties - nastavnie logovania, zaujimave je log4j.logger.org.hibernate=info (nastavnie na debug bude vypisovat debug hlasky)

v zdrojakoch by nemalo byt potreba nic upravit, pustajte ten program s parametrom "insert" alebo "select"

Dufam, ze som na nic nezabudol

Kovo