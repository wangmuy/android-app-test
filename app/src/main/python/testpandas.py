import pandas as pd
def main():
    data = {
        "product": ["A", "B", "C", "C", "D"],
        "price": [22000, 27000, 25000, 29000, 35000],
        "year": [2014, 2015, 2016, 2017, 2018],
    }
    df = pd.DataFrame(data)
    stats_numeric = df["price"].describe()
    print(stats_numeric)