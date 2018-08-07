# LynisParser
Il parser serve per :
    importare log di Lynis
    lanciare lynis 
    parsare il report e inserirlo in un db sqlite (per ora poi vediamo...)
    permettere il confronto con altri report precenetemente importati(questo bho forse generando dei report ad hoc vediamo poi come)
sarebbe bello anche (magari dopo)
    creare/pulire il db
    visualizzare categorie e test esistenti
    effettuare statistiche/ricerche (qualche filtro o groupby diciamo)
    export in vari formati
dopo dopo dopo e se ci va
    aggiungere una gui 
    aggiungere webservice (loro lo hanno gia per la versione a pagamento )
    

    
    
Note 
la mia idea molto semplice è questa
1)si popolano le tabelle con le categorie e i test
2)si usano quelle tabelle per popolare la tabella con i report audit e si salvano su una nuova tabella che ha la FK con quelle precendenti
3)negli audit successivi si prendono le tabelle con categorie e test e ci si aggiungono quelli nuovi che non sono presenti 
4)si rifa il punto 2




