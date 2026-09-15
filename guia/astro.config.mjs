// @ts-check
import { defineConfig } from 'astro/config';
import starlight from '@astrojs/starlight';

// https://astro.build/config
export default defineConfig({
	site: 'https://mkarajallo.github.io',
	base: '/pokedex',
	integrations: [
		starlight({
			title: 'Pokédex — Android desde cero',
			defaultLocale: 'root',
			locales: {
				root: { label: 'Español', lang: 'es' },
			},
			sidebar: [
				{
					label: 'Capítulos',
					items: [{ autogenerate: { directory: 'capitulos' } }],
				},
				{
					label: 'Recursos',
					items: [
						{ label: 'Glosario', slug: 'recursos/glosario' },
						{ label: 'Enunciado del TP', slug: 'recursos/enunciado' },
					],
				},
			],
		}),
	],
});
