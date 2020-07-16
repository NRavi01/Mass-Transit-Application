import pandas as pd
import numpy as np

import sqlite3
from sqlite3 import Error

from sklearn.ensemble import RandomForestRegressor
from sklearn.model_selection import train_test_split
from sklearn.decomposition import PCA

def queryAndProcess():
    
    db = r"MartaSimulation.db"
    
    connnect = None
    try:
        connect = sqlite3.connect(db)
    except Error as e:
        print(e)
        
    qs = pd.read_sql_query("SELECT * FROM stop", connect)
    
    labels = qs["riders"]

    iter_id = qs["id"]

    qs.drop(qs.columns[0], axis=1, inplace = True)
    qs.drop("riders", axis = 1, inplace = True)
    
    qs = pd.get_dummies(qs)
    
    qs = np.array(qs)

    return qs, labels, iter_id

def main():
    qs, labels, iter_id  = queryAndProcess()

    x_train, x_test, y_train, y_test = train_test_split(qs, labels, test_size=0.25)

    rfr = RandomForestRegressor()
    rfr.fit(x_train, y_train)

    all_pred = rfr.predict(qs)

    np.savetxt('predictions.csv', [iter_id, all_pred], delimiter=",", fmt="%s")

if __name__ == '__main__':
    main()