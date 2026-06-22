require('dotenv').config();

const express = require('express');
const cors = require('cors');
const helmet = require('helmet');
const morgan = require('morgan');
const scoresRouter = require('./routes/scores');

const app = express();
const PORT = process.env.PORT || 3000;
const corsOrigin = process.env.CORS_ORIGIN || '*';

app.use(helmet());
app.use(cors({ origin: corsOrigin }));
app.use(express.json({ limit: '1mb' }));
app.use(morgan('dev'));

app.get('/', (req, res) => {
  res.json({
    ok: true,
    app: 'ESCOMSurfers Server',
    message: 'Servidor funcionando correctamente.'
  });
});

app.get('/api/health', (req, res) => {
  res.json({
    ok: true,
    status: 'online',
    timestamp: new Date().toISOString()
  });
});

app.use('/api', scoresRouter);

app.use((req, res) => {
  res.status(404).json({
    ok: false,
    message: 'Ruta no encontrada.'
  });
});

app.use((error, req, res, next) => {
  console.error('Error no controlado:', error);
  res.status(500).json({
    ok: false,
    message: 'Error interno del servidor.'
  });
});

app.listen(PORT, '0.0.0.0', () => {
  console.log(`ESCOMSurfers Server escuchando en http://0.0.0.0:${PORT}`);
});
