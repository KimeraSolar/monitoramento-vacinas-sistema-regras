# -*- coding: utf-8 -*-
"""
Created on Wed Jun 28 10:45:47 2023

@author: lorena
"""

#%%

import pandas
from datetime import datetime
import seaborn as sns
import matplotlib.pyplot as plt

#%%
n_testes = 15
dataframes = []

for teste_id in range(1, n_testes + 1):
    
    alertas_file = 'results/performance_data/teste_{:03d}_alertas.csv'
    df_alertas = pandas.read_csv(alertas_file.format(teste_id))
    df_alertas = df_alertas.set_axis(['initial_time', 'final_time'], axis='columns', copy=False)
    df_alertas = df_alertas.assign(event_type='Alerta')
    
    perigos_file = 'results/performance_data/teste_{:03d}_perigos.csv'
    df_perigos = pandas.read_csv(perigos_file.format(teste_id))
    df_perigos = df_perigos.set_axis(['initial_time', 'final_time'], axis='columns', copy=False)
    df_perigos = df_perigos.assign(event_type='Perigo')
    
    df = pandas.concat([df_alertas, df_perigos])
    df = df.assign(test_id=((teste_id - 1) // 3) + 1)
    df = df.assign(real_test_id = teste_id)
    dataframes.append(df)
    
df = pandas.concat(dataframes)

#%%
date_format = '%Y-%m-%d %H:%M:%S.%f'

df['initial_time'] = df['initial_time'].apply(lambda date_string : datetime.strptime(date_string, date_format))
df['final_time'] = df['final_time'].apply(lambda date_string : datetime.strptime(date_string, date_format))
df['delta_time'] = df['final_time'] - df['initial_time']
df['millis'] = df['delta_time'].apply(lambda delta_time : delta_time.total_seconds()*1000)

#%%

df.to_csv('results/performance_data/analitcs.csv')

#%%  

df_describer = df.groupby('test_id')[['delta_time', 'millis']].describe()
df_describer.to_csv('results/performance_data/statistics.csv')

#%%

df_describer_2 = df.groupby('real_test_id')[['delta_time', 'millis']].describe()
df_counters = df.groupby(['real_test_id', 'event_type'])['event_type'].count()

#%%

df = pandas.read_csv('results/performance_data/analitcs.csv')
df['test_id'] = df['test_id'].apply(lambda idx : 'teste ' + str(idx))

#%%
plt.clf()
ax = sns.boxplot(y="test_id", x="millis", data=df)
ax.set(xlabel='Log do Tempo (ms)', ylabel='Teste')
plt.xscale('log')
ax.spines[['right', 'top']].set_visible(False)
plt.show()
    
#%%
plt_df = df[['test_id', 'millis']]

#%%

plt.clf()
ax = sns.barplot(y='test_id', x='millis', data=plt_df, errorbar=None)
ax.set(xlabel='Log da Média do Tempo (ms)', ylabel='Teste')
plt.xscale('log')
ax.spines[['right', 'top']].set_visible(False)
ax.bar_label(ax.containers[0], fmt='%.1f')
plt.show()
