Det är viktigt att man inte går och pillar i transaktionsobjekt hur som helst!

Gör man fel sorts ändring så blir objekten inkompatibla med tidigare rapporterad information
vilket leder till att man förlorar all data!

Kortfattat tillåter java.io.Serializable att man lägger till och tar bort fält, men inte att man byter namn på dem
så länge serialVersionUID har samma statiska värde.